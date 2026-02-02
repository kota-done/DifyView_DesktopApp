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
import window_interface.BridgeCallback;


/**
 * チャット機能Logicクラス
 * @author 
 * @version 1.2
 * 修正：
 * 2026/1/24 WindowControllerクラス生成メソッドの外だし
 * 2026/1/30 テストによる確認のため、各メソッドの外だし、WindowControllerの生成タイミングをwrapper生成直後に移動。
 * →
 */
public class WindowLogic {
	//設定ファイルパス（固定）
	private final static String PROPS_PATH = "/resources/app.properties";

	//プロパティオブジェクト
	private Properties props = null;
	//設定ファイルDtoのオブジェクト
	private AppSettingDto configDto = null;
	//ウィンドウの画面表示クラスのオブジェクト
//	2026/01/31 WebEngine生成メソッド追加時に合わせて、フィールドでのオブジェクトが不要なためコメントアウト
//	private WindowView windowview = null;
//	//ウィンドウの情報を保持するオブジェクト
//	private WebView webview = null;
//	//画面機能のクラスのオブジェクト　メソッド呼び出し用
	
	private WindowController windowController;
	//API通信クラスのオブジェクト　メソッド呼び出し用
	private DifyApiClient apiClient;
	// WebViewエンジンのラッパー（onWindowSet内で初期化）
	private WebEngineWrapper wrapper;
	//
	private final UiIniWrapper uiRunnable = new UiIniWrapper();
	//ブリッジクラスのオブジェクト　ロジックで保持する用
	private JavaBridge bridge;
	// 入力バリデータ（Controllerへ注入）
	private InputValidator inputValidator;
	//ロガーオブジェクト。アプリの起動・終了・異常を出力。処理の詳細は各処理で実装。
	private static final Logger logger = LoggerFactory.getLogger(WindowLogic.class);

	/**
	 * WindowController 生成メソッド
	 *  テスト差し込み用に追加
	 * @param wrapper        WebEngineのラッパー（UI 更新呼び出し用）
	 * @param apiClient      Dify API通信クラス
	 * @param uiRunnable     JavaFX UIスレッド実行ラッパー
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
	 * WebEngineWrapper 生成メソッド
	 *  テスト差し込み用に追加
	 * @param windowView     WindowViewクラスオブジェクト 
	 * @return 生成されたWebEngineWrapperインスタンス
	 */
	protected WebEngineWrapper createEngineWrapper(WindowView windowView) {
		return new WebEngineWrapper(windowView.getView().getEngine());
	}
	/**
	 *  コールバックオブジェクトセットメソッドの起動
	 *  テスト差し込み用に追加
	 * @param bridgecallback 匿名クラス生成の
	 * @param windowView     W
	 * @return void
	 */
	protected void registerStaticCallback(BridgeCallback bridgeCallback) {
		WindowView.setStaticCallback(bridgeCallback);
	}
	/**
	 * windowLaunch 画面初期化処理の起動
	 *  テスト差し込み用に追加
	 * @param title          ウィンドウのタイトル
	 * @param width          ウィンドウの横幅
	 * @param height         ウィンドウの高さ
	 * @return void
	 */
	protected void windowLaunch(String title,String width,String height) {
		//引数；1、WindowView（Application実装クラス）2〜、ウィンドウ設定値
		Application.launch(WindowView.class, title, width, height);
	}

	/**
	 * ウィンドウ表示を制御する実行メソッド
	 */
	public void execute() {
		//プロパティオブジェクトとその初期化
		try {
			//app.propertiesの読み込み
			props = CommonFunction.load(PROPS_PATH);
			//設定ファイルDtoの初期化
			configDto = SettingLoader.startSettings(props);
			//API通信オブジェクトの初期化
			apiClient = new DifyApiClient(configDto.getApiUrl(), configDto.getApiKey());
			//入力バリデータの初期化（Controllerへ注入）
			inputValidator = new InputValidator();
			
			//コールバックオブジェクトをセット。匿名クラスでBridgeCallbackの	1回限り実装。
			registerStaticCallback(new BridgeCallback() {
				/**
				 * JSオブジェクトにブリッジを登録する
				 * @param view 登録対象のWebViewオブジェクト（JSオブジェクトを内包している）
				 */
				@Override
				public void onWindowSet(WindowView view) {
					//windowview = view;
					//webview = windowview.getView();

					// JS Bridge登録
					bridge = new JavaBridge(this);

					// DOMとJSのロードが完了したタイミングでBridge登録とJS呼び出し
					try {
						//初期化したUIオブジェクトからラッパーオブジェクトの初期化
						wrapper = createEngineWrapper(view);
						//コントローラークラスの初期化
						windowController = createWindowController(wrapper, apiClient, uiRunnable, inputValidator);
						wrapper.registerBridge("JavaBridge", bridge);
						wrapper.call("initChat()");
						logger.info("ウィンドウ起動処理：正常起動");
					} catch (Exception e) {
						logger.error("初期化セット起動エラー：異常終了", e);
						throw new IllegalStateException("ウィンドウ初期セットでエラー発生：" + e);
					}
				}

				/**
				 * コントローラークラスのAPI通信処理起動（ブリッジクラスからの中継）
				 * @param input ブリッジから受け取ったユーザー入力文字列
				 */
				@Override
				public void onUserInput(String input) {
				    if (windowController == null) {
				        logger.error("APIディスパッチ：起動失敗");
				        return;
				    }
				    logger.error("APIディスパッチ：起動");
				    windowController.onSendMessage(input);
				}
			});

			//WindowViewに設定値Dtoを渡すための変数を用意
			String setTitle = configDto.getTitle();
			String setWidth = String.valueOf(configDto.getWindWidth());
			String setHeight = String.valueOf(configDto.getWindHeight());

			//ウィンドウ初期化処理呼び出し　
			windowLaunch(setTitle, setWidth, setHeight);

		} catch (Exception e) {
			logger.error("ロジックエラー：️", e);
			throw new IllegalStateException("ロジック内で例外発生：" + e);
		}
	}
}