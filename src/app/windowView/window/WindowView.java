package app.windowView.window;

import java.util.List;

import javafx.application.Application;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WindowView extends Application{

	@Override
	public void start(Stage args) throws Exception {
		
		//呼び出しメソッドlaunchの引数受け取り　
		List<String> laParams = getParameters().getRaw();
		
		//引数の中身が存在しない場合エラー
		if(laParams.size() == 0) {
			throw new IllegalStateException("引数に必要な項目がありません。");
		}
		
		WebView webView = new WebView();
		
		WebEngine webEngine = new WebEngine();
		
		//URLを読み込む
	}
}
