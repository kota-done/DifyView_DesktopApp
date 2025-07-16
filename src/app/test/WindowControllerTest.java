package app.test;

import static java.lang.System.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.Test;

import app.windowView.api.DifyApiClient;
import app.windowView.window.UiIniWrapper;
import app.windowView.window.WebEngineWrapper;
import app.windowView.window.WindowController;

public class WindowControllerTest {
	// ウィンドウ用モック生成
	WebEngineWrapper mockEngine = mock(WebEngineWrapper.class);
	//APIクラスのモック
	DifyApiClient mockApiClient = mock(DifyApiClient.class);
	//UI処理用のモック
	UiIniWrapper mockUiRunnable = mock(UiIniWrapper.class);
	// モックのcontroller
	WindowController controller = new WindowController(mockEngine, mockApiClient, mockUiRunnable);

	/**
	 *テストパターン　正常系　onSendMessage処理から正しくappendChatChunkおよびonChatCompleteが呼び出される。 
	 */
	@Test
	public void test1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン1";
		out.println("**********************************************");
		out.println(resultOutput + "が開始されました。");

		// 非同期処理の完了待機用
		CountDownLatch latch = new CountDownLatch(2); // appendMsg + completeMsg

		try {

			// UI初期化用ラッパーのモック作成（runLaterを即実行）
			doAnswer(invocation -> {
				Runnable runnable = invocation.getArgument(0);
				runnable.run(); // 即実行
				latch.countDown();
				return null;
			}).when(mockUiRunnable).runLater(any());

			// APIモック設定
			doAnswer(invocation -> {
				Consumer<String> chunkConsumer = invocation.getArgument(1);
				Runnable completeCallback = invocation.getArgument(2);

				chunkConsumer.accept("テストチャンク");
				completeCallback.run();
				return null;
			}).when(mockApiClient).streamingMsg(any(), any(), any());

			// テスト実行
			controller.onSendMessage("テストメッセージ");

			// 処理完了まで待機（最大2秒）
			boolean completed = latch.await(2, TimeUnit.SECONDS);
			assertTrue("非同期処理が2秒以内に完了しませんでした", completed);

			// JavaScript呼び出しの確認（）
			verify(mockEngine).call(eq("appendMsg(テストチャンク)"));
			verify(mockEngine).call(eq("completeMsg()"));

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　正常系　複数チャンク受信時でもパターン1と同様の結果が得られる。
	 */
	@Test
	public void test2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2";
		out.println("**********************************************");
		out.println(resultOutput + "が開始されました。");

		// 非同期処理の完了待機用
		CountDownLatch latch = new CountDownLatch(3); // appendMsg×2 + completeMsg

		try {

			// UI初期化用ラッパーのモック作成（runLaterを即実行）
			doAnswer(invocation -> {
				Runnable runnable = invocation.getArgument(0);
				runnable.run(); // 即実行
				latch.countDown();
				return null;
			}).when(mockUiRunnable).runLater(any());

			// APIモック設定
			doAnswer(invocation -> {
				Consumer<String> chunkConsumer = invocation.getArgument(1);
				Runnable completeCallback = invocation.getArgument(2);

				chunkConsumer.accept("テストチャンク1");
				chunkConsumer.accept("テストチャンク2");
				completeCallback.run();
				return null;
			}).when(mockApiClient).streamingMsg(any(), any(), any());

			// テスト実行
			controller.onSendMessage("テストメッセージ");

			// 処理完了まで待機（最大2秒）
			boolean completed = latch.await(2, TimeUnit.SECONDS);
			assertTrue("非同期処理が2秒以内に完了しませんでした", completed);

			// JavaScript呼び出しの確認（）
			verify(mockEngine).call(eq("appendMsg(テストチャンク1)"));
			verify(mockEngine).call(eq("appendMsg(テストチャンク2)"));
			verify(mockEngine).call(eq("completeMsg()"));

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　正常系　複数チャンク受信時でもパターン1と同様の結果が得られる。
	 */
	@Test
	public void test3() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3";
		out.println("**********************************************");
		out.println(resultOutput + "が開始されました。");

		// 非同期処理の完了待機用
		CountDownLatch latch = new CountDownLatch(1); // showError

		try {

			// UI初期化用ラッパーのモック作成（runLaterを即実行）
			doAnswer(invocation -> {
				Runnable runnable = invocation.getArgument(0);
				runnable.run(); // 即実行
				latch.countDown();
				return null;
			}).when(mockUiRunnable).runLater(any());

			// APIモック設定
			doThrow(new RuntimeException("API通信の失敗")).when(mockApiClient).streamingMsg(any(), any(), any());

			// テスト実行
			controller.onSendMessage("テストメッセージ");

			// 処理完了まで待機（最大2秒）
			boolean completed = latch.await(2, TimeUnit.SECONDS);
			assertTrue("非同期処理が2秒以内に完了しませんでした", completed);
			// JavaScript呼び出しの確認（）
			verify(mockEngine).call(eq("showError(エラーが発生しました: API通信の失敗)"));

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}
}
