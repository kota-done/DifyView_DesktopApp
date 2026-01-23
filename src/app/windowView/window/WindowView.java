package app.windowView.window;

import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import window_interface.BridgeCallback;

/**
 * デスクトップアプリのウィンドウ表示クラス。
 * @author 
 * @version 1.0
 */
public class WindowView extends Application {
	//WebViewオブジェクト。ロジッククラスへの戻し用
	private final WebView webView = new WebView();

	//ブリッジのラッパークラスオブジェクト
	private static BridgeCallback staticCallback;
	
	//ロガーオブジェクト
	//private final Logger logger = LoggerFactory.getLogger(WindowView.class);
	
	//HTMLファイルのパス取得:クラスパス配下
	private final String ChATWINDOW_PATH = "/resources/window/window_chatBot.html";
	
	/************
	* メソッド名：ウェブオブジェクト取得
	* 処理内容：ウェブオブジェクトを取り出す。
	* @return webView 表示したウィンドウの表示内容をもつオブジェクト
	* @throws IllegalStateException　ウェブオブジェクトがNULLの場合、例外。
	/************/
	public WebView getView() {
		if (webView == null) {
			throw new IllegalStateException("webViewが初期化されていない。");
		}
		return webView;
	}

	/************
	* メソッド名：ウェブオブジェクト取得
	* 処理内容：ウェブオブジェクトを取り出す。
	* @return webView 表示したウィンドウの表示内容をもつオブジェクト
	/************/
	public static void setStaticCallback(BridgeCallback callback) {
		staticCallback = callback;
	}
	//	
	//	/************
	//	* メソッド名：設定ファイルDtoセットメソッド
	//	* 処理内容：ロジッククラス経由で設定ファイルDtoを受け取る。
	//	/************/
	//	public void setDto(AppSettingDto dto,JavaBridge bridge,BridgeCallback bc) {
	//		this.dto = dto;
	//		this.bridge = bridge;
	//		this.bridgeCall = bc;
	//	}

	/************
	* メソッド名：ウィンドウ表示処理
	* 処理内容：JavaFXのApplicationクラスの抽象メソッドの実装。ウィンドウの設定値をセットする。
	* @param stage 呼び出し元のlaunchメソッドの第2引数、可変長のString型。：Dtoで注入するので使用しない。
	* @return void
	* @throws IllegalStateException 実行時の引数のうち整数の項目が整数でなかった場合例外。
	/************/
	@Override
	public void start(Stage stage) throws Exception {

		//呼び出しメソッドlaunchの引数受け取り(1,title 2,windWidth 3,windHeight)全てStrin型
		List<String> laParams = getParameters().getRaw();

		//引数の中身が存在しない場合エラー
		if (laParams.size() == 0) {
			throw new IllegalStateException("引数に必要な項目がありません。");
		}
		String title = laParams.get(0);
		int width, height;

		//整数に格納
		try {
			width = Integer.parseInt(laParams.get(1));
			height = Integer.parseInt(laParams.get(2));

		} catch (NumberFormatException e) {
			//整数以外例外
			throw new IllegalStateException("幅または高さが整数ではありません。:" + e.getMessage());
		}
		//ウィンドウ表示HTMLのURL取得
		URL url = getClass().getResource(ChATWINDOW_PATH);

		//WebEngineの初期化ロードのチェック。ロードが完了してから、ロジッククラスのブリッジセット処理を呼び出す。
		webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				//ロジッククラスからコールバック用のオブジェクトがセットされているかチェック。
				if (staticCallback != null) {
					//セットされていれば、ロジッククラスにWindoViewオブジェクトを渡してブリッジセットメソッド起動。
					staticCallback.onWindowSet(this);
				} else {
					throw new IllegalStateException("BridgeCallbackがセットされていません。");
				}
			}
		});

		if (url == null) {
			throw new IllegalStateException("HTMLファイルが見つかりません" + ChATWINDOW_PATH);
		} else {
			webView.getEngine().load(url.toExternalForm());

			//ウィンドウ表示
			Scene scene = new Scene(webView, width, height);
			stage.setTitle(title);
			stage.setScene(scene);
			stage.show();
		}
	}
}
