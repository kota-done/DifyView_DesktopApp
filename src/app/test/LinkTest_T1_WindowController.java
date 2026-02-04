package app.test;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import app.windowView.api.DifyApiClient;
import app.windowView.validation.InputValidator;
import app.windowView.window.UiIniWrapper;
import app.windowView.window.WebEngineWrapper;
import app.windowView.window.WindowController;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

public class LinkTest_T1_WindowController {

	private MockWebServer server;

	@Before
	public void setUp() throws Exception {
		server = new MockWebServer();
		server.start();
	}

	@After
	public void tearDown() throws Exception {
		if (server != null)
			server.shutdown();
	}

	/**
	 * 【テスト種別】LT-N1
	 * 【テスト観点】正常系（最小ストリーミング成功パターン）
	 *
	 * テスト内容：
	 *  - message → message_end を含むストリーミングレスポンスを受信する
	 *  - Controller.onSendMessage() 起点で UI 更新処理まで到達することを確認
	 *
	 * 確認対象：
	 *  - webEngine.call() 経由の JavaScript 呼び出し
	 *
	 * 期待結果：
	 *  - appendMsg() がチャンク2回呼ばれる
	 *  - completeMsg() が1回呼ばれる
	 *  - showError() は呼ばれない
	 */
	@Test
	public void LT_N1_streaming2chunk_complete() throws Exception {

		// チャンク2回分＋終了通知チャンク
		String body = "data: {\"event\":\"message\",\"answer\":\"これは\"}\n" +
				"data: {\"event\":\"message\",\"answer\":\"テスト\"}\n" +
				"data: {\"event\":\"message_end\"}\n";

		server.enqueue(new MockResponse()
				.setResponseCode(200)
				.addHeader("Content-Type", "text/event-stream")
				.setBody(body));

		// LTの観測箇所：webEngine.call
		WebEngineWrapper webEngine = mock(WebEngineWrapper.class);

		// runLater即時実行
		UiIniWrapper ui = new UiIniWrapper() {
			@Override
			public void runLater(Runnable r) {
				r.run();
			}
		};

		// 完了検知：completeMsg() が呼ばれたら終了
		CountDownLatch done = new CountDownLatch(1);
		List<String> calls = new CopyOnWriteArrayList<>();
		//JavaScriptが呼ばれたらcallsに格納＋completeMsgが呼ばれたらカウントダウン終了。
		doAnswer(inv -> {
			String js = inv.getArgument(0, String.class);
			calls.add(js);

			if (js.contains("completeMsg()")) {
				done.countDown();
			}
			return null;
		}).when(webEngine).call(anyString());

		// --- 実DifyApiClient（URLはMockWebServerへ）
		String apiUrl = server.url("/").toString(); // フルURLにPOST
		DifyApiClient apiClient = new DifyApiClient(apiUrl, "dummy-key");

		WindowController controller = new WindowController(webEngine, apiClient, ui, new InputValidator());

		// 実行
		controller.onSendMessage("hi");

		// completeMsgが呼ばれた＝API通信処理が完了したことの確認。
		assertTrue("completeMsg() が呼ばれませんでした",
				done.await(2, TimeUnit.SECONDS));

		// Js呼び出し回数検証（文言一致ではなく種別確認）
		long appendCount = calls.stream().filter(s -> s.contains("appendMsg(")).count();
		long completeCount = calls.stream().filter(s -> s.contains("completeMsg()")).count();
		long errorCount = calls.stream().filter(s -> s.contains("showError(")).count();

		assertEquals("appendMsg 呼び出し回数が不正", 2, appendCount);
		assertEquals("completeMsg 呼び出し回数が不正", 1, completeCount);
		assertEquals("showError は呼ばれてはいけない", 0, errorCount);
	}

	/**
	 * 【テスト種別】LT-N2
	 * 【テスト観点】正常系（連続チャンク受信）
	 *
	 * テスト内容：
	 *  - message を10回連続で受信し、最後に message_end を受信するストリーミングレスポンスを想定
	 *  - Controller.onSendMessage() 起点で UI 更新処理まで到達することを確認
	 *
	 * 確認対象：
	 *  - webEngine.call() 経由の JavaScript 呼び出し
	 *
	 * 期待結果：
	 *  - appendMsg() が10回呼ばれる
	 *  - completeMsg() が1回呼ばれる
	 *  - showError() は呼ばれない
	 */
	@Test
	public void LT_N2_streaming10chunks_complete() throws Exception {
		// チャンク10回分＋終了通知チャンク
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 10; i++) {
			sb.append("data: {\"event\":\"message\",\"answer\":\"")
					.append(i)
					.append("\"}\n");
		}
		sb.append("data: {\"event\":\"message_end\"}\n");

		server.enqueue(new MockResponse()
				.setResponseCode(200)
				.addHeader("Content-Type", "text/event-stream")
				.setBody(sb.toString()));

		// LTの観測箇所：webEngine.call
		WebEngineWrapper webEngine = mock(WebEngineWrapper.class);

		// runLater即時実行
		UiIniWrapper ui = new UiIniWrapper() {
			@Override
			public void runLater(Runnable r) {
				r.run();
			}
		};

		// 完了検知：completeMsg()が呼ばれたら終了
		CountDownLatch done = new CountDownLatch(1);
		List<String> calls = new CopyOnWriteArrayList<>();

		// JavaScriptが呼ばれたらcallsに格納＋completeMsgが呼ばれたらカウントダウン終了。
		doAnswer(inv -> {
			String js = inv.getArgument(0, String.class);
			calls.add(js);

			if (js.contains("completeMsg()")) {
				done.countDown();
			}
			return null;
		}).when(webEngine).call(anyString());

		// DifyApiClient
		String apiUrl = server.url("/").toString(); // フルURLへPOST
		DifyApiClient apiClient = new DifyApiClient(apiUrl, "dummy-key");

		WindowController controller = new WindowController(webEngine, apiClient, ui, new InputValidator());

		// 実行
		controller.onSendMessage("start");

		// completeMsg() が呼ばれること（＝処理完了）と、テストが無限待機しないことを確認
		assertTrue("completeMsg()が呼ばれませんでした", done.await(2, TimeUnit.SECONDS));

		// Js呼び出し回数検証（文言一致ではなく種別確認）
		long appendCount = calls.stream().filter(s -> s.contains("appendMsg(")).count();
		long completeCount = calls.stream().filter(s -> s.contains("completeMsg()")).count();
		long errorCount = calls.stream().filter(s -> s.contains("showError(")).count();

		assertEquals("appendMsg 呼び出し回数が不正", 10, appendCount);
		assertEquals("completeMsg 呼び出し回数が不正", 1, completeCount);
		assertEquals("showError は呼ばれてはいけない", 0, errorCount);
	}

	/**
	 * 【テスト種別】LT-E1
	 * 【テスト観点】異常系（HTTPエラー応答）
	 *
	 * テスト内容：
	 *  - API が HTTP 401 を返却した場合を想定
	 *  - Controller.onSendMessage() 起点で UI 更新処理（エラー表示）まで到達することを確認
	 *
	 * 確認対象：
	 *  - webEngine.call() 経由の JavaScript 呼び出し
	 *  
	 * 期待結果：
	 *  - showError() が1回呼ばれる
	 *  - appendMsg() は呼ばれない
	 *  - completeMsg() は呼ばれない
	 */
	@Test
	public void LT_E1_http401_showError() throws Exception {

		server.enqueue(new MockResponse()
				.setResponseCode(401)
				.addHeader("Content-Type", "application/json"));

		// LTの観測箇所：webEngine.call
		WebEngineWrapper webEngine = mock(WebEngineWrapper.class);

		// runLater即時実行
		UiIniWrapper ui = new UiIniWrapper() {
			@Override
			public void runLater(Runnable r) {
				r.run();
			}
		};

		// 完了検知：showError()が呼ばれたら終了
		CountDownLatch done = new CountDownLatch(1);
		List<String> calls = new CopyOnWriteArrayList<>();

		// JavaScriptが呼ばれたらcallsに格納＋showErrorが呼ばれたらカウントダウン終了。
		doAnswer(inv -> {
			String js = inv.getArgument(0, String.class);
			calls.add(js);

			if (js.contains("showError(")) {
				done.countDown();
			}
			return null;
		}).when(webEngine).call(anyString());

		// DifyApiClient
		String apiUrl = server.url("/").toString(); // フルURLへPOST
		DifyApiClient apiClient = new DifyApiClient(apiUrl, "dummy-key");

		WindowController controller = new WindowController(webEngine, apiClient, ui, new InputValidator());

		// 実行
		controller.onSendMessage("start");

		// showError() が呼ばれること（＝エラー通知がUI呼び出しに到達）と、テストが無限待機しないことを確認
		assertTrue("showError()が呼ばれませんでした", done.await(2, TimeUnit.SECONDS));

		// Js呼び出し回数検証（文言一致ではなく種別確認）
		long errorCount = calls.stream().filter(s -> s.contains("showError(")).count();
		long appendCount = calls.stream().filter(s -> s.contains("appendMsg(")).count();
		long completeCount = calls.stream().filter(s -> s.contains("completeMsg()")).count();

		assertEquals("showError 呼び出し回数が不正", 1, errorCount);
		assertEquals("appendMsg は呼ばれてはいけない", 0, appendCount);
		assertEquals("completeMsg は呼ばれてはいけない", 0, completeCount);
	}

	/**
	 * 【テスト種別】LT-E2
	 * 【テスト観点】異常系（エンドポイント誤り：404）
	 *
	 * テスト内容：
	 *  - API が HTTP 404 を返却した場合を想定（APIパス誤り）
	 *  - Controller.onSendMessage() 起点で UI エラー表示処理まで到達することを確認
	 *
	 * 確認対象：
	 *  - webEngine.call() 経由の JavaScript 呼び出し
	 *
	 * 期待結果：
	 *  - showError() が1回呼ばれる
	 *  - appendMsg() は呼ばれない
	 *  - completeMsg() は呼ばれない
	 */
	@Test
	public void LT_E2_http404_showError() throws Exception {

		server.enqueue(new MockResponse()
				.setResponseCode(404)
				.addHeader("Content-Type", "application/json"));

		// LTの観測箇所：webEngine.call
		WebEngineWrapper webEngine = mock(WebEngineWrapper.class);

		// runLater即時実行
		UiIniWrapper ui = new UiIniWrapper() {
			@Override
			public void runLater(Runnable r) {
				r.run();
			}
		};

		// 完了検知：showError()が呼ばれたら終了
		CountDownLatch done = new CountDownLatch(1);
		List<String> calls = new CopyOnWriteArrayList<>();

		// JavaScriptが呼ばれたらcallsに格納＋showErrorが呼ばれたらカウントダウン終了。
		doAnswer(inv -> {
			String js = inv.getArgument(0, String.class);
			calls.add(js);

			if (js.contains("showError(")) {
				done.countDown();
			}
			return null;
		}).when(webEngine).call(anyString());

		// DifyApiClient
		String apiUrl = server.url("/").toString(); // フルURLへPOST
		DifyApiClient apiClient = new DifyApiClient(apiUrl, "dummy-key");

		WindowController controller = new WindowController(webEngine, apiClient, ui, new InputValidator());

		// 実行
		controller.onSendMessage("start");

		// showError() が呼ばれること（＝エラー通知がUI呼び出しに到達）と、テストが無限待機しないことを確認
		assertTrue("showError()が呼ばれませんでした", done.await(2, TimeUnit.SECONDS));

		// Js呼び出し回数検証（文言一致ではなく種別確認）
		long errorCount = calls.stream().filter(s -> s.contains("showError(")).count();
		long appendCount = calls.stream().filter(s -> s.contains("appendMsg(")).count();
		long completeCount = calls.stream().filter(s -> s.contains("completeMsg()")).count();

		assertEquals("showError 呼び出し回数が不正", 1, errorCount);
		assertEquals("appendMsg は呼ばれてはいけない", 0, appendCount);
		assertEquals("completeMsg は呼ばれてはいけない", 0, completeCount);
	}

	/**
	 * 【テスト種別】LT-E3
	 * 【テスト観点】異常系（通信失敗：接続直後に切断 / onFailure）
	 *
	 * テスト内容：
	 *  - 接続直後にソケット切断（HTTP行/ヘッダ/ボディは一切送られない）を再現
	 *  - Controller.onSendMessage() 起点で UI エラー表示処理まで到達することを確認
	 *
	 * 確認対象：
	 *  - webEngine.call() 経由の JavaScript 呼び出し
	 *
	 * 期待結果：
	 *  - showError() が1回呼ばれる
	 *  - appendMsg() は呼ばれない
	 *  - completeMsg() は呼ばれない
	 */
	@Test
	public void LT_E3_disconnect() throws Exception {

		// 接続直後にソケット切断（HTTP行/ヘッダ/ボディは一切送られない）
		server.enqueue(new MockResponse()
				.setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

		// LTの観測箇所：webEngine.call
		WebEngineWrapper webEngine = mock(WebEngineWrapper.class);

		// runLater即時実行
		UiIniWrapper ui = new UiIniWrapper() {
			@Override
			public void runLater(Runnable r) {
				r.run();
			}
		};

		// showError() の呼び出し検知（1回のみを期待）
		CountDownLatch done = new CountDownLatch(1);
		List<String> calls = new CopyOnWriteArrayList<>();

		doAnswer(inv -> {
			String js = inv.getArgument(0, String.class);
			calls.add(js);

			if (js.contains("showError(")) {
				done.countDown();
			}
			return null;
		}).when(webEngine).call(anyString());

		// DifyApiClient（URLはMockWebServerへ）
		String apiUrl = server.url("/chat").toString();
		DifyApiClient apiClient = new DifyApiClient(apiUrl, "dummy-key");

		WindowController controller = new WindowController(webEngine, apiClient, ui, new InputValidator());

		// 実行
		controller.onSendMessage("start");

		// showError() が呼ばれること（＝エラー通知がUI呼び出しに到達）と、テストが無限待機しないことを確認
		assertTrue("showError()が呼ばれませんでした", done.await(2, TimeUnit.SECONDS));

		// Js呼び出し回数検証（文言一致ではなく種別確認）
		long errorCount = calls.stream().filter(s -> s.contains("showError(")).count();
		long appendCount = calls.stream().filter(s -> s.contains("appendMsg(")).count();
		long completeCount = calls.stream().filter(s -> s.contains("completeMsg()")).count();

		assertEquals("showError 呼び出し回数が不正", 1, errorCount);
		assertEquals("appendMsg は呼ばれてはいけない", 0, appendCount);
		assertEquals("completeMsg は呼ばれてはいけない", 0, completeCount);
	}

	/**
	 * 【テスト種別】LT-E4
	 * 【テスト観点】異常系（ストリーム途中異常：不正JSON混在）
	 *
	 * テスト内容：
	 *  - message を正常に複数回受信した後、不正JSON（パース不能）を混在させる
	 *  - Controller.onSendMessage() 起点で、正常分の UI 更新後にエラー表示へ遷移することを確認
	 *
	 * 確認対象：
	 *  - webEngine.call() 経由の JavaScript 呼び出し
	 *
	 * 期待結果：
	 *  - appendMsg() が正常チャンク分だけ呼ばれる
	 *  - showError() が1回呼ばれる
	 *  - completeMsg() は呼ばれない
	 */
	@Test
	public void LT_E4_invalid_json() throws Exception {

		// 正常チャンク2回分 + 不正JSON（括弧未完） + 
		// （不正JSON行で例外になり、その後の行は処理されないため message_end があっても completeMsg には到達しないが実際のチャンクの形式を用意）
		String body = "data: {\"event\":\"message\",\"answer\":\"これは\"}\n" +
				"data: {\"event\":\"message\",\"answer\":\"正常です\"}\n" +
				"data: {\"event\":\"message\",\"answer\":\"不正\" \n" + // ← 不正JSON（括弧未完）
				"data: {\"event\":\"message_end\"}\n";

		server.enqueue(new MockResponse()
				.setResponseCode(200)
				.addHeader("Content-Type", "text/event-stream")
				.setBody(body));

		// LTの観測箇所：webEngine.call
		WebEngineWrapper webEngine = mock(WebEngineWrapper.class);

		// runLater即時実行
		UiIniWrapper ui = new UiIniWrapper() {
			@Override
			public void runLater(Runnable r) {
				r.run();
			}
		};

		// showError() の呼び出し検知
		CountDownLatch done = new CountDownLatch(1);
		List<String> calls = new CopyOnWriteArrayList<>();

		doAnswer(inv -> {
			String js = inv.getArgument(0, String.class);
			calls.add(js);

			if (js.contains("showError(")) {
				done.countDown();
			}
			return null;
		}).when(webEngine).call(anyString());

		// DifyApiClient（URLはMockWebServerへ）
		String apiUrl = server.url("/chat").toString();
		DifyApiClient apiClient = new DifyApiClient(apiUrl, "dummy-key");

		WindowController controller = new WindowController(webEngine, apiClient, ui, new InputValidator());

		// 実行
		controller.onSendMessage("start");

		// showError() が呼ばれること（＝異常終了へ遷移）と、テストが無限待機しないことを確認
		assertTrue("showError()が呼ばれませんでした", done.await(2, TimeUnit.SECONDS));

		// Js呼び出し回数検証（文言一致ではなく種別確認）
		long appendCount = calls.stream().filter(s -> s.contains("appendMsg(")).count();
		long errorCount = calls.stream().filter(s -> s.contains("showError(")).count();
		long completeCount = calls.stream().filter(s -> s.contains("completeMsg()")).count();

		// 正常チャンク2件分だけ UI 更新される想定
		assertEquals("appendMsg 呼び出し回数が不正", 2, appendCount);
		assertEquals("showError 呼び出し回数が不正", 1, errorCount);
		assertEquals("completeMsg は呼ばれてはいけない", 0, completeCount);
	}
}