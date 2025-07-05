/**
 * 
 */
package app.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.gson.Gson;

import app.windowView.api.DifyApiClient;
import app.windowView.api.DifyRequestDto;

/**
 * 
 */
public class DifyApiClientTest {
	
	//API URL
	private String testAPI_URL;
	//API key
	private String testapiKey;
	
	//リクエストDto
	DifyRequestDto testDto;
	
	Gson gson = new Gson();

	@Test
	public void test() {
		 // 待機用ラッチ（1回の完了を待つ）
        CountDownLatch latch = new CountDownLatch(1);
		
		//動作検証用API
		testAPI_URL = "https://api.dify.ai/v1/chat-messages";
		//API key　7/6破棄済み。
		testapiKey = "app-U0mzkxjHHMKMHOLUqipN8ScJ";
		
		//リクエストDtoの生成
		testDto = new DifyRequestDto("君の役割を答えてくれ");
		
		System.out.println(gson.toJson(testDto));
		
		//DifyAPIClientクラスのインスタンス準備
		DifyApiClient testClient = new DifyApiClient(testAPI_URL, testapiKey);
		try {
			testClient.streamingMsg(
					testDto, 
					chunk ->{
						System.out.println("チャンク受信："+chunk);
					}, 
					()->{
						System.out.println("完了通知:チャット終了");
						 latch.countDown();  // 完了したら待機解除
					});
			// 非同期完了まで最大10秒待機（タイムアウトも検出可能）
	        boolean completed = latch.await(10, TimeUnit.SECONDS);
	        assert completed : "ストリーミングがタイムアウトしました";
			
		} catch (Exception e) {
			throw new IllegalStateException("何かしらのエラー："+e);
		}
		
	}

}
