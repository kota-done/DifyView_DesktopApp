package app.windowView.window;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
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
	/************
	* メソッド名：オブジェクトセットメソッド
	* 処理内容：ブリッジクラスのオブジェクトをJSobjectにセットするメソッド
	* @param name JSでの呼び出し用の名前
	* @param bridgeObject セットするクラスのオブジェクト
	/************/
	public void registerBridge(String name, Object bridgeObject) {
	    JSObject window = (JSObject) webEngine.executeScript("window");
	    window.setMember(name, bridgeObject);
	}
	
	public WebEngine getEngine() {
	    return webEngine;
	}
}