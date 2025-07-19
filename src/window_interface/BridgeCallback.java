package window_interface;

import app.windowView.window.WindowView;

/**
 * JavaBridgeからロジッククラスへの密結合防止のインターフェース：責務の委譲先の抽象表現（契約）
 * 
 */
public interface BridgeCallback {

	
	/**
	 * WindowViewオブジェクトの受け渡し
	 * param view 呼び出し元が所持しているWindowViewオブジェクト
	 */
	void onWindowSet(WindowView view);

	void onUserInput(String input);
}

