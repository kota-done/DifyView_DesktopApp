package app.windowView.window;

import app.window_interface.BridgeCallback;

public class JavaBridge {
	
	//ロジッククラスのメソッドを呼び出すための抽象化オブジェクト
	private final BridgeCallback bridgecall;

	/**
	 * コンストラクタ。
	 * @param bc Java側でユーザー入力を処理するロジッククラスの抽象オブジェクト
	 */
	public JavaBridge(BridgeCallback bc) {
		this.bridgecall = bc;
	}

	/**
	 * JavaScriptからのメッセージをJava側に送信するためのメソッド。
	 * @param input ユーザー入力の文字列（nullと空白はフロント側でチェック）
	 */
	public void sendToJava(String input) {
		bridgecall.onUserInput(input);
	}
}
