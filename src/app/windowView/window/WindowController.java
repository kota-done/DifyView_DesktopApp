package app.windowView.window;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.CommonFunction;
import app.windowView.api.DifyApiClient;
import app.windowView.api.DifyRequestDto;
import app.windowView.validation.InputValidator;

public class WindowController {

	// 表示ウィンドウのWebEngineラッパー
	private final WebEngineWrapper webEngine;
	// API通信用インスタンス
	private final DifyApiClient apiClient;
	// UIスレッド実行ラッパー
	private final UiIniWrapper uiRunnable;
	// 入力バリデータ
	private final InputValidator validator;

	// ロガー
	private static final Logger logger = LoggerFactory.getLogger(WindowController.class);

	// API通信の重複起動防止フラグ（送信中trueを設定）
	private final AtomicBoolean enqueFlg = new AtomicBoolean(false);

	/************
	 * メソッド名：引数付きコンストラクタ
	 * 処理内容：ロジッククラスで初期化する際に、現在表示中のウィンドウのオブジェクトを取得する。
	 * @param webEngine 現在表示中のウィンドウのWebEngineラッパー
	 * @param apiClient Dify APIクライアント
	 * @param r UIスレッド実行ラッパー
	 * @param validator 入力バリデータ
	 ************/
	public WindowController(WebEngineWrapper webEngine, DifyApiClient apiClient, UiIniWrapper r,
			InputValidator validator) {
		this.webEngine = webEngine;
		this.apiClient = apiClient;
		this.uiRunnable = r;
		this.validator = validator;
	}

	/************
	 * メソッド名：イベントハンドラーメソッド
	 * 処理内容：JSのイベント発火をロジッククラス経由で受け取る処理。API通信処理を呼び出す。
	 * @param msg ユーザー入力メッセージ
	 ************/
	public void onSendMessage(String msg) {

		// 受付の事実（本文は出さない）
		logger.info("input.accepted len={}", msg == null ? 0 : msg.length());

		// 二重送信防止：すでに送信中なら即終了
		if (!enqueFlg.compareAndSet(false, true)) {
			logger.info("通信処理実行中のため起動を拒否。");
			return;
		}

		final String normalized;
		try {
			// 入力メッセージのバリデーションチェック
			normalized = validator.validateExecute(msg);
		} catch (IllegalArgumentException e) {
			// 入力NG → UIエラー表示して復帰
			uiRunnable.runLater(() -> showError(e.getMessage()));
			enqueFlg.set(false);
			return;
		} catch (Exception e) {
			// 想定外例外
			logger.error("バリデーションチェックに該当したためエラー：", e);
			uiRunnable.runLater(() -> showError("入力チェックでエラーが発生しました。"));
			enqueFlg.set(false);
			return;
		}

		// リクエストDto生成（正規化後文字列を投入）
		DifyRequestDto dto = new DifyRequestDto(normalized);

		// API通信用スレッド作成（通信終了後に破棄）
		Thread communicationThread = new Thread(() -> {
			try {
				apiClient.streamingMsg(
						dto,
						chunk -> uiRunnable.runLater(() -> appendChatChunk(chunk)),
						() -> uiRunnable.runLater(this::onChatComplete),
						err -> uiRunnable.runLater(() -> showError(err)));
			} catch (Exception e) {
				logger.error("API通信ディスパッチ：異常終了", e);
				uiRunnable.runLater(() -> showError("通信処理でエラーが発生しました。"));
			} finally {
				// 実行フラグのリセット
				enqueFlg.set(false);
			}
		});
		communicationThread.setDaemon(true);
		communicationThread.start();
	}

	/************
	 * メソッド名：エラーメッセージ表示
	 * 処理内容：API通信またはUI更新処理内でエラーが発生したらエラーメッセージを表示する処理を呼び出す。
	 * @param err エラー内容
	 ************/
	private void showError(String err) {
		String msg = "エラーが発生しました: " + err;
		webEngine.call("showError(" + CommonFunction.escapeForJS(msg) + ")");
	}

	/************
	 * メソッド名：受信チャンク表示
	 * 処理内容：受信したチャンク毎に表示処理を呼び出す
	 * @param chunk Difyからのレスポンス
	 ************/
	private void appendChatChunk(String chunk) {
		webEngine.call("appendMsg(" + CommonFunction.escapeForJS(chunk) + ")");
	}

	/************
	 * メソッド名：受信完了メッセージ表示
	 * 処理内容：全てのチャンクの受信が完了した旨を表示する処理を呼び出す。
	 ************/
	private void onChatComplete() {
		logger.info("API通信ディスパッチ：正常終了");
		webEngine.call("completeMsg()");
	}
}