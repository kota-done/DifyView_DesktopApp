package app.test;

import static org.mockito.Mockito.*;

import org.junit.Test;

import app.windowView.window.JavaBridge;
import app.windowView.window.WindowController;
import window_interface.BridgeCallback;

public class LinkTest_T2_BridgeToController {

	/**
	 * 【テスト種別】LT-N1
	 * 【テスト観点】正常系（Bridge → Controller の最小連携）
	 *
	 * テスト内容：
	 *  - JavaBridge.sendToJava(input) を起点に、
	 *    BridgeCallback.onUserInput(input) 経由で Controller.onSendMessage(input) が呼ばれることを確認する
	 *
	 * 確認対象：
	 *  - WindowController.onSendMessage() が呼ばれた事実（呼び出し回数・引数）
	 *
	 * 期待結果：
	 *  - onSendMessage() が1回呼ばれる
	 *  - 追加の呼び出しは発生しない（verifyNoMoreInteractions）
	 */
	@Test
	public void LT_N1_bridge_to_onSendMessage() {
		// Controllerは「呼ばれた事実」だけ観測するのでモック化
		WindowController controller = mock(WindowController.class);

		// callbackはテスト用の最小実装（onUserInputでcontrollerを呼ぶ）
		BridgeCallback callback = new BridgeCallback() {
			@Override
			public void onWindowSet(app.windowView.window.WindowView view) {
			}

			@Override
			public void onUserInput(String input) {
				controller.onSendMessage(input);
			}
		};
		JavaBridge bridge = new JavaBridge(callback);

		// 実行
		bridge.sendToJava("LTN1 input");

		// 観測：Controllerが1回呼ばれること
		verify(controller, times(1)).onSendMessage("LTN1 input");
		verifyNoMoreInteractions(controller);
	}
}
