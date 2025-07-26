package app.test;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import app.util.CommonFunction;
import app.windowView.api.DifyApiClient;
import app.windowView.api.DifyRequestDto;
import app.windowView.config.AppSettingDto;
import app.windowView.config.SettingLoader;

public class AlldousaCheck {
	//
	private final static String PROPS_PATH = "/resources/app.properties";

	//API URL
	private String testAPI_URL;
	//API key
	private String testapiKey;

	//リクエストDto
	DifyRequestDto testDto;
	//
	private Properties props = null;
	//
	private AppSettingDto configDto = null;

	@Test
	public void test() {
		// 待機用ラッチ（1回の完了を待つ）
		CountDownLatch latch = new CountDownLatch(1);
		try {
			props = CommonFunction.load(PROPS_PATH);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//設定ファイルDtoの初期化
		configDto = SettingLoader.startSettings(props);

		//動作検証用API
		testAPI_URL = configDto.getApiUrl();
		//API key
		testapiKey = configDto.getApiKey();

		//リクエストDtoの生成
		testDto = new DifyRequestDto("君の役割を答えてくれ");

		//DifyAPIClientクラスのインスタンス準備
		DifyApiClient testClient = new DifyApiClient(testAPI_URL, testapiKey);
		try {
			testClient.streamingMsg(
					testDto,
					chunk -> {
						System.out.println("チャンク受信：" + chunk);
					},
					() -> {
						System.out.println("完了通知:チャット終了");
						latch.countDown(); // 完了したら待機解除
					});
			// 非同期完了まで最大10秒待機（タイムアウトも検出可能）
			boolean completed = latch.await(10, TimeUnit.SECONDS);
			assert completed : "ストリーミングがタイムアウトしました";

		} catch (Exception e) {
			throw new IllegalStateException("何かしらのエラー：" + e);
		}

	}

}
