package app.windowView.window;

import java.util.Properties;

import app.util.CommonFunction;
import app.windowView.api.DifyApiClient;
import app.windowView.config.AppSettingDto;
import app.windowView.config.SettingLoader;
import javafx.application.Application;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import window_interface.BridgeCallback;

public class WindowLogic {
	//
	private final static String PROPS_PATH = "/resources/app.properties";

	//
	private Properties props = null;
	//
	private AppSettingDto configDto = null;
	//
	private WindowView windowview = null;
	//
	private WebView webview = null;
	//
	private WindowController windowController = null;
	//
	private DifyApiClient apiClient;
	// WebViewエンジンのラッパー（onWindowSet内で初期化）
	private WebEngineWrapper wrapper;
	//
	private UiIniWrapper uiRunnable = new UiIniWrapper();

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
					JavaBridge bridge = new JavaBridge(this);
					// DOMとJSのロードが完了したタイミングでBridge登録とJS呼び出し
					try {
						JSObject js = (JSObject) webview.getEngine().executeScript("window");
//						System.out.println("ブリッジ設定メソッド起動");
						js.setMember("JavaBridge", bridge);
						// JavaScriptの初期化関数を呼び出す（この時点でJavaBridgeは登録済）
						js.eval("initChat()");
					} catch (Exception e) {
						System.out.println("なんかのエラー：" + e);
					}

					//初期化したUIオブジェクトからラッパーオブジェクトの初期化
					wrapper = new WebEngineWrapper(webview.getEngine());
					//コントローラークラスの初期化
					windowController = new WindowController(wrapper, apiClient, uiRunnable);
				}

				/**
				 * コントローラークラスのAPI通信処理起動（ブリッジクラスからの中継）
				 * @param input ブリッジから受け取ったユーザー入力文字列
				 */
				@Override
				public void onUserInput(String input) {
					//API通信処理呼び出し
					windowController.onSendMessage(input);
				};
			});

			//WindowViewに設定値Dtoを渡すための変数を用意
			String setTitle = configDto.getTitle();
			String setWidth = String.valueOf(configDto.getWindWidth());
			String setHeight = String.valueOf(configDto.getWindHeight());

			//ウィンドウ初期化処理呼び出し　引数；1、WindowView（Application実装クラス）2〜、ウィンドウ設定値
			Application.launch(WindowView.class, setTitle, setWidth, setHeight);

		} catch (Exception e) {
			throw new IllegalStateException("ロジック内で例外発生：" + e);
		}
		//ウィンドウの初期化処理の呼び出し：WindowView　引数：設定ファイルDto
	}

}