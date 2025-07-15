package app.test;

import static java.lang.System.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.junit.Test;

import app.windowView.api.DifyApiClient;
import app.windowView.window.UiIniWrapper;
import app.windowView.window.WebEngineWrapper;
import app.windowView.window.WindowController;

public class WindowControllerTest {

	@Test
	public void test1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン1_1";
		
		out.println("**********************************************");
		out.println(resultOutput + "が開始されました。");
		
		// 非同期処理の完了待機用
        CountDownLatch latch = new CountDownLatch(2); // appendMsg + completeMsg
		
		try {
			// ウィンドウ用モック生成
		    WebEngineWrapper mockEngine = mock(WebEngineWrapper.class);

		    DifyApiClient mockApiClient = mock(DifyApiClient.class);
		    
		    //UI処理用のモック
		    UiIniWrapper mockUiRunnable = mock(UiIniWrapper.class);
		    
		    // モックのcontroller
		    WindowController controller = new WindowController(mockEngine, mockApiClient,mockUiRunnable);
		    
		    // UI初期化用ラッパーのモック作成（runLaterを即実行）
            doAnswer(invocation -> {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();  // 即実行
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
            latch.await();

		    // JavaScript呼び出しの確認（エスケープ処理済み）
		    verify(mockEngine).call(contains("appendMsg"));
		    verify(mockEngine).call(contains("completeMsg"));
		    
		    out.println(resultOutput + "が正常終了しました。");
			
		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		}finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	    
	}

}
