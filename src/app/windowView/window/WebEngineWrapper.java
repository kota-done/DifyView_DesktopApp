package app.windowView.window;

import javafx.scene.web.WebEngine;
import window_interface.JsCall;

public class WebEngineWrapper implements JsCall{
	private final WebEngine webEngine;
	
	public WebEngineWrapper(WebEngine webEngine) {
		this.webEngine = webEngine;
	}
	
	/************
	* メソッド名：JsCallの実装メソッド
	* 処理内容：引数のJavaScriptを呼びだす。
	* @param js 呼び出し対象のJavaScript（文字列）
	/************/
	@Override
	public void call(String js) {
		webEngine.executeScript(js);
	}
	
	public WebEngine getEngine() {
	    return webEngine;
	}
}