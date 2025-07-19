package app.windowView.window;

import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * デスクトップアプリのウィンドウ表示クラス。
 * @author 
 * @version 1.0
 */
public class WindowView extends Application {
	//
	WebView webView = new WebView();

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
	* メソッド名：ウィンドウ表示処理
	* 処理内容：JavaFXのApplicationクラスの抽象メソッドの実装。ウィンドウの設定値をセットする。
	* @param stage 呼び出し元のlaunchメソッドの第2引数、可変長のString型。
	* @return void
	* @throws IllegalStateException 実行時の引数のうち整数の項目が整数でなかった場合例外。
	/************/
	@Override
	public void start(Stage stage) throws Exception {

		//呼び出しメソッドlaunchの引数受け取り(1,title 2,windUrl 3,windWidth 4,windHeight)全てStrin型
		List<String> laParams = getParameters().getRaw();

		//引数の中身が存在しない場合エラー
		if (laParams.size() == 0) {
			throw new IllegalStateException("引数に必要な項目がありません。");
		}
		String title = laParams.get(0);
		String difyUrl = laParams.get(1);
		int width, height;

		//整数に格納
		try {
			width = Integer.parseInt(laParams.get(2));
			height = Integer.parseInt(laParams.get(3));

		} catch (NumberFormatException e) {
			//整数以外例外
			throw new IllegalStateException("幅または高さが整数ではありません。:" + e.getMessage());
		}
		//HTMLファイル取り込み
		String htmlPath = "/resources/window/window_chatBot.html";
		URL url = getClass().getResource(htmlPath);

		if (url == null) {
			throw new IllegalStateException("HTMLファイルが見つかりません" + htmlPath);
		}
		//HTML読み込み
		webView.getEngine().load(url.toExternalForm());

		;

		//ウィンドウ表示
		Scene scene = new Scene(webView, width, height);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}
}
