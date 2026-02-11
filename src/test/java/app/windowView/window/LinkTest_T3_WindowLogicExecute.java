package app.windowView.window;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.InOrder;

import app.windowView.api.DifyApiClient;
import app.windowView.validation.InputValidator;
import app.window_interface.BridgeCallback;

public class LinkTest_T3_WindowLogicExecute {

	/**
	 * 【テスト種別】LT-N1
	 * 【テスト観点】正常系（Logic 初期化フローの整合性）
	 *
	 * テスト内容：
	 *  - WindowLogic.execute() により BridgeCallback が登録される
	 *  - BridgeCallback.onWindowSet() を起点として、
	 *    以下の初期化フローが順序通りに実行されることを確認する
	 *
	 *    1. WebEngineWrapperの生成
	 *    2. WindowControllerの生成
	 *    3. JavaBridgeのregisterBridge
	 *    4. initChat()の呼び出し
	 *
	 * 確認対象：
	 *  - WebEngineWrapper.registerBridge()の呼び出し
	 *  - WebEngineWrapper.call("initChat()")の呼び出し
	 *  - WindowController生成が1回行われること
	 *  - windowLaunch() が呼ばれること（回数・引数）
	 *
	 * 期待結果：
	 *  - 初期化処理が例外なく完了する
	 *  - 各処理が1回ずつ、正しい順序で呼び出される
	 *  - windowLaunch() が 1 回呼ばれる
	 */
	@Test
	public void LT_N1_windowInitialization_flow() {

		// テスト用メソッドを継承したWindowLogic
		TestableWindowLogic logic = new TestableWindowLogic();
		// 画面表示不要なため、Mockで代用
		WindowView view = mock(WindowView.class);

		// 実行
		logic.execute();

		//① コールバックが登録されていること
		assertNotNull(logic.testCallback);

		//② windowLaunch が1回呼ばれること
		assertEquals(1, logic.windowLaunchCalled);
		assertNotNull(logic.windowLaunchTitle);
		assertNotNull(logic.windowLaunchWidth);
		assertNotNull(logic.windowLaunchHeight);

		//WindowView.onWindowSetを手動で起動※Application.launch経由で起動しないため
		logic.testCallback.onWindowSet(view);

		//③ Wrapper生成/Controller生成の回数
		assertEquals(1, logic.createWrapperCalled);
		assertEquals(1, logic.createControllerCalled);

		//④ registerBridge → initChat() の順序確認
		InOrder inOrder = inOrder(logic.mockWrapper);
		inOrder.verify(logic.mockWrapper).registerBridge(eq("JavaBridge"), any());
		inOrder.verify(logic.mockWrapper).call("initChat()");
		//⑤WebEngineWrapperのモックメソッドが④の検証済み以外で呼び出されていないこと。
		verifyNoMoreInteractions(logic.mockWrapper);
	}

	/**
	 * 【テスト種別】LT-E1
	 * 【テスト観点】異常系（WebEngineWrapper 生成失敗）
	 *
	 * テスト内容：
	 *  - BridgeCallback.onWindowSet(view) 実行時に
	 *    WebEngineWrapper 生成で例外が発生する状況を作る
	 *
	 * 確認対象：
	 *  - 初期化処理が IllegalStateException として失敗すること
	 *  - 後続処理（Controller生成・JS呼び出し）が実行されないこと
	 *  - windowLaunch() は execute() 時点で呼ばれること
	 *
	 * 期待結果：
	 *  - IllegalStateException がスローされる
	 *  - WindowController は生成されない
	 *  - wrapper には一切触れない（verifyNoInteractions）
	 *  - windowLaunch() が 1 回呼ばれる
	 */
	@Test
	public void LT_E1_createEngineWrapper_failed() {

		// テスト用メソッドを継承したWindowLogic
		TestableWindowLogic logic = new TestableWindowLogic();
		// 画面表示不要なため、Mockで代用
		WindowView view = mock(WindowView.class);
		
		//例外発生用のExceptionインスタンス仕込み
		logic.throwOnCreateWrapper = new RuntimeException("wrapper生成失敗");

		// execute
		logic.execute();
		
		//①コールバックが登録されていること
		assertNotNull(logic.testCallback);
		assertEquals(1, logic.windowLaunchCalled);
		//②onWindowSetでcreateEngineWrapperがRuntimeExceptionにより例外となること
		assertThrows(IllegalStateException.class, () -> logic.testCallback.onWindowSet(view));
		
		//③Wrapper生成/Controller生成の回数
		assertEquals(1, logic.createWrapperCalled);
		assertEquals(0, logic.createControllerCalled);
		//④WebEngineWrapperのモックメソッドが一度も呼び出されていないこと。
		verifyNoInteractions(logic.mockWrapper);
	}

	/**
	 * 【テスト種別】LT-E2
	 * 【テスト観点】異常系（Bridge 登録処理失敗）
	 *
	 * テスト内容：
	 *  - registerBridge() 実行時に例外が発生する状況を作る
	 *
	 * 確認対象：
	 *  - initChat() が呼ばれないこと
	 *  - 初期化処理が IllegalStateException として終了すること
	 *  - windowLaunch() は execute() 時点で呼ばれること
	 *
	 * 期待結果：
	 *  - registerBridge() は 1 回呼ばれる
	 *  - initChat() は呼ばれない
	 *  - IllegalStateException がスローされる
	 *  - windowLaunch() が 1 回呼ばれる
	 */
	@Test
	public void LT_E2_registerBridge_failed() {

	    //テスト用メソッドを継承したWindowLogic
	    TestableWindowLogic logic = new TestableWindowLogic();
	    //画面表示不要なため、Mockで代用
	    WindowView view = mock(WindowView.class);

	    //registerBridge()が呼ばれた瞬間に例外を投げるようための仕込み
	    doThrow(new RuntimeException("ウィンドウにJSオブジェクト仕込みエラー"))
	            .when(logic.mockWrapper).registerBridge(eq("JavaBridge"), any());

	    //実行
	    logic.execute();

	    //①コールバックが登録されていること
	    assertNotNull(logic.testCallback);

	    //②windowLaunchが1回呼ばれること
	    assertEquals(1, logic.windowLaunchCalled);

	    //③onWindowSet起点で初期化が開始されるが、registerBridgeで例外になりIllegalStateExceptionで落ちること
	    assertThrows(IllegalStateException.class,
	            () -> logic.testCallback.onWindowSet(view));

	    //④Wrapper生成/Controller生成の回数
	    assertEquals(1, logic.createWrapperCalled);
	    assertEquals(1, logic.createControllerCalled);

	    //⑤registerBridge()は1回呼ばれる（失敗点がここであること）
	    verify(logic.mockWrapper, times(1))
	            .registerBridge(eq("JavaBridge"), any());

	    //⑥ initChat()は呼ばれない
	    verify(logic.mockWrapper, never())
	            .call("initChat()");

	    //⑦ 余計な呼び出しが無いことを厳密にしたいなら追加
	    verifyNoMoreInteractions(logic.mockWrapper);
	}

	/**
	 * テスト用 WindowLogic
	 *  - JavaFX 起動を抑止
	 *  - 生成メソッドを差し替えて観測点を作る
	 *  - windowLaunch 呼び出し回数・引数を記録する
	 */
	static class TestableWindowLogic extends WindowLogic {

		BridgeCallback testCallback;

		WebEngineWrapper mockWrapper = mock(WebEngineWrapper.class);
		WindowController mockController = mock(WindowController.class);
		//呼び出し回数セット
		int createWrapperCalled = 0;
		int createControllerCalled = 0;
		int windowLaunchCalled = 0;
		
		//引数設定
		String windowLaunchTitle;
		String windowLaunchWidth;
		String windowLaunchHeight;
		//例外発生用スロー
		RuntimeException throwOnCreateWrapper;

		@Override
		protected void registerStaticCallback(BridgeCallback callback) {
			this.testCallback = callback;
		}

		@Override
		protected WebEngineWrapper createEngineWrapper(WindowView view) {
			createWrapperCalled++;
			//テストソースの想定と異なり、nullの場合例外発生
			if (throwOnCreateWrapper != null) {
				throw throwOnCreateWrapper;
			}
			return mockWrapper;
		}

		@Override
		protected WindowController createWindowController(
				WebEngineWrapper wrapper,
				DifyApiClient apiClient,
				UiIniWrapper uiRunnable,
				InputValidator inputValidator) {
			createControllerCalled++;
			return mockController;
		}

		@Override
		protected void windowLaunch(String title, String width, String height) {
			// JavaFX 起動はテストでは行わない
			windowLaunchCalled++;
			this.windowLaunchTitle = title;
			this.windowLaunchWidth = width;
			this.windowLaunchHeight = height;
		}
	}
}