package app.windowView.window;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.util.CommonFunction;
import app.windowView.api.DifyApiClient;
import app.windowView.config.AppSettingDto;
import app.windowView.config.SettingLoader;
import app.windowView.validation.InputValidator;
import javafx.application.Application;
import javafx.scene.web.WebView;
import window_interface.BridgeCallback;

public class WindowLogic {
	//
	private final static String PROPS_PATH = "/resources/app.properties";

	//プロパティオブジェクト
	private Properties props = null;
	//設定ファイルDtoのオブジェクト
	private AppSettingDto configDto = null;
	//ウィンドウの画面表示クラスのオブジェクト
	private WindowView windowview = null;
	//ウィンドウの情報を保持するオブジェクト
	private WebView webview = null;
	//画面機能のクラスのオブジェクト　メソッド呼び出し用
	private WindowController windowController;
	//API通信クラスのオブジェクト　メソッド呼び出し用
	private DifyApiClient apiClient;
	// WebViewエンジンのラッパー（onWindowSet内で初期化）
	private WebEngineWrapper wrapper;
	//
	private UiIniWrapper uiRunnable = new UiIniWrapper();

	//ブリッジクラスのオブジェクト　ロジックで保持する用
	private JavaBridge bridge;

	// 入力バリデータ（Controllerへ注入）
	private InputValidator inputValidator;

	//ロガーオブジェクト。アプリの起動・終了・異常を出力。処理の詳細は各処理で実装。
	private static final Logger logger = LoggerFactory.getLogger(WindowLogic.class);

	/**
	 * WindowController 生成メソッド
	 *
	 * @param wrapper        WebEngine のラッパー（UI 更新呼び出し用）
	 * @param apiClient      Dify API 通信クラス
	 * @param uiRunnable     JavaFX UI スレッド実行ラッパー
	 * @param inputValidator 入力バリデータ（Fail-fast）
	 * @return 生成されたControllerインスタンス
	 */
	protected WindowController createWindowController(
			WebEngineWrapper wrapper,
			DifyApiClient apiClient,
			UiIniWrapper uiRunnable,
			InputValidator inputValidator) {

		return new WindowController(wrapper, apiClient, uiRunnable, inputValidator);
	}

	/**
	 * ウィンドウ表示を制御する実行メソッド
	 */
	public void execute() {
		//プロパティオブジェクトとその初期化
		try {
			props = CommonFunction.load(PROPS_PATH);

			//設定ファイルDtoの初期化
			configDto = SettingLoader.startSettings(props);

			//API通信オブジェクトの初期化
			apiClient = new DifyApiClient(configDto.getApiUrl(), configDto.getApiKey());

			//入力バリデータの初期化（Controllerへ注入）
			inputValidator = new InputValidator();

			//コールバックオブジェクトをセット。匿名クラスでコールバック後のブリッジセットメソッド実装。
			WindowView.setStaticCallback(new BridgeCallback() {
				/**
				 * JSオブジェクトにブリッジを登録する
				 * @param view 登録対象のWebViewオブジェクト（JSオブジェクトを内包している）
				 */
				@Override
				public void onWindowSet(WindowView view) {
					windowview = view;
					webview = windowview.getView();

					// JS Bridge登録
					bridge = new JavaBridge(this);

					// DOMとJSのロードが完了したタイミングでBridge登録とJS呼び出し
					try {
						//初期化したUIオブジェクトからラッパーオブジェクトの初期化
						wrapper = new WebEngineWrapper(webview.getEngine());
						wrapper.registerBridge("JavaBridge", bridge);
						wrapper.call("initChat()");
						logger.info("ウィンドウ起動処理：正常起動");
					} catch (Exception e) {
						logger.error("初期化セット起動エラー：異常終了", e);
						throw new IllegalStateException("ウィンドウ初期セットでエラー発生：" + e);
					}

					//コントローラークラスの初期化
					windowController = createWindowController(wrapper, apiClient, uiRunnable, inputValidator);
				}

				/**
				 * コントローラークラスのAPI通信処理起動（ブリッジクラスからの中継）
				 * @param input ブリッジから受け取ったユーザー入力文字列
				 */
				@Override
				public void onUserInput(String input) {
					//ユーザー入力受け付けから、メソッド起動
					logger.info("API通信ディスパッチ：起動");
					//API通信処理呼び出し
					windowController.onSendMessage(input);
				}
			});

			//WindowViewに設定値Dtoを渡すための変数を用意
			String setTitle = configDto.getTitle();
			String setWidth = String.valueOf(configDto.getWindWidth());
			String setHeight = String.valueOf(configDto.getWindHeight());

			//ウィンドウ初期化処理呼び出し　引数；1、WindowView（Application実装クラス）2〜、ウィンドウ設定値
			Application.launch(WindowView.class, setTitle, setWidth, setHeight);

		} catch (Exception e) {
			logger.error("ロジックエラー：️", e);
			throw new IllegalStateException("ロジック内で例外発生：" + e);
		}
	}
}