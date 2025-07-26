package app.windowView.window;

import app.util.CommonFunction;
import app.windowView.api.DifyApiClient;
import app.windowView.api.DifyRequestDto;

public class WindowController {
	
	//表示ウィンドウのWindowEngineのラッパーオブジェクト
	private final WebEngineWrapper webEngine;
	//APIt通信用のインスタンス　APIのURLやキー情報を内包している。
	private final DifyApiClient apiClient;
	//UI処理スレッドのラッパーオブジェクト
	private final UiIniWrapper uiRunnable;
	
	/************
	* メソッド名：引数付きコンストラクタ
	* 処理内容：ロジッククラスで初期化する際に、現在表示中のウィンドウのオブジェクトを取得する。
	* @param view　ロジッククラスのフィールドにあるWindowViewオブジェクト
	/************/
	public WindowController(WebEngineWrapper webEngine,DifyApiClient apiClient,UiIniWrapper r) {
		this.webEngine = webEngine;
		this.apiClient = apiClient;
		this.uiRunnable = r;
	}
	/************
	* メソッド名：イベントハンドラーメソッド
	* 処理内容：JSのイベント発火をロジッククラス経由で受け取る処理。API通信処理を呼び出す。
	* @param msg ユーザー入力メッセージ
	/************/
	public void onSendMessage(String msg) {
		
		//リクエストDto生成
		DifyRequestDto dto = new DifyRequestDto(msg);
		//API通信用のスレッド作成。通信終了後に破棄。
		Thread communicationThread = new Thread(() -> {
		    try {
		        apiClient.streamingMsg(
		            dto,
		            chunk -> uiRunnable.runLater(() -> appendChatChunk(chunk)),
		            () -> uiRunnable.runLater(this::onChatComplete)
		        );
		    } catch (Exception e) {
		    	uiRunnable.runLater(() -> showError(e));
		    }
		});
		communicationThread.setDaemon(true); // アプリ終了と同時に停止するよう設定
		communicationThread.start(); // 実行。
}
	/************
	* メソッド名：エラーメッセージ表示
	* 処理内容：API通信またはUI更新処理内でエラーが発生したらエラーメッセージを表示する処理を呼び出す。
	* @param e 
	/************/
	private void showError(Exception e) {
		String msg = "エラーが発生しました: " + e.getMessage();
	    webEngine.call("showError(" + CommonFunction.escapeForJS(msg) + ")");
	}
	/************
	* メソッド名：受信チャンク表示
	* 処理内容：受信したチャンク毎に表示処理を呼び出す
	* @param chunk　Difyからのレスポンス
	/************/
	private void appendChatChunk(String chunk) {
		webEngine.call("appendMsg("+ CommonFunction.escapeForJS(chunk)+")");
	}
	/************
	* メソッド名：受信完了メッセージ表示
	* 処理内容：全てのチャンクの受信が完了した旨を表示する処理を呼び出す。
	/************/
	private void onChatComplete() {
		//System.out.println("最終チャンクを認識し、JS呼び出しは可能。");
		webEngine.call("completeMsg()");
	}
}
	
