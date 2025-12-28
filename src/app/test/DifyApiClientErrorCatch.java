package app.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import app.windowView.api.DifyApiClient;
import app.windowView.api.DifyRequestDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
//追加のimport
import okhttp3.mockwebserver.SocketPolicy;

/**
 * ##概要##
 * DifyApiClientのエラー挙動確認テストクラス
 * - 通信エラーやHTTP500、JSON内エラーが呼び出し元に伝播しないことを確認
 *  * DTO（DifyResponseDto）で利用している/契約上重要なフィールド：
 * - event : message_end 判定に必須
 * - answer: message の表示に必須（ただし空混入はスキップ方針）
 *
 * optional扱い（現時点の要件では必須化しない）：
 * - id / message_id / conversation_id / retriever_resources
 
 * ##改修##
 * - 20251213
 * ・各テストのTry-CatchおよびAtomicBoolean thrown削除。
 * ※onErrorおよびonFailureで例外を制御しているのでCatchできない。＝不要と判断。
 */
public class DifyApiClientErrorCatch {

	private static MockWebServer mockServer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// モックサーバ起動
		mockServer = new MockWebServer();
		mockServer.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		mockServer.shutdown();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * HTTP 500 応答時の動作確認
	 * → 呼び出し元に例外が伝わらないこと、onChunk/onComplete未発火を確認
	 * 期待：合格
	 *  - onChunk は発火しない
	 *  - onComplete は発火しない
	 */
	@Test
	public void testHttp500_NotPropagated() throws IOException, InterruptedException {
		// モックレスポンスを登録
		mockServer.enqueue(new MockResponse()
				.setResponseCode(500)
				.setBody("{\"message\":\"server error\"}"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		// コールバック制御
		CountDownLatch latchChunk = new CountDownLatch(1);
		CountDownLatch latchComplete = new CountDownLatch(1);
		//修正後ソース追加分
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("ErrorCatch01_http500"),
				s -> latchChunk.countDown(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		// HTTP500は例外ではなく onError が呼び出されていること
		assertTrue("onErrorが呼ばれていない", latchError.await(1500, TimeUnit.MILLISECONDS));
		// onError で終了する想定のため、chunk/complete は呼ばれないこと
		assertEquals("onChunkが呼ばれた", 1L, latchChunk.getCount());
		assertEquals("onCompleteが呼ばれた", 1L, latchComplete.getCount());

		// **** 修正（2025/12/13）
		//可読性が悪いので、最初はawaitで実行を待つがその後は呼び出し回数のアサーションに変更		
		// コールバックが発火しないこと
		//assertFalse("onChunkが呼ばれた", latchChunk.await(300, TimeUnit.MILLISECONDS));
		//assertFalse("onCompleteが呼ばれた", latchComplete.await(300, TimeUnit.MILLISECONDS));
		//修正ソース追加分
		//assertTrue("onErrorが呼ばれている", latchError.await(500, TimeUnit.MILLISECONDS));
		// **** 修正（2025/12/13）
	}

	/**
	 * 途中で切断：通信接続後、すぐに切断 → onFailure 経路を検証
	 * 期待：合格
	 *  - DISCONNECT_AT_START：onChunkは呼ばれない（固定）
	 *  - onChunk は発火しない
	 *  - onComplete は発火しない
	 */
	@Test
	public void testOnFailureExecute() throws Exception {
		// 接続直後にソケットを切断（HTTP行/ヘッダ/ボディは一切送られない）
		mockServer.enqueue(new MockResponse()
				.setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		CountDownLatch latchChunk = new CountDownLatch(1);
		CountDownLatch latchComplete = new CountDownLatch(1);
		//修正後ソース追加分
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("ErrorCatch02_OnFailureExecute"),
				s -> latchChunk.countDown(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		// 同期点：onError が来るまで待つ
		assertTrue("onErrorが呼ばれていない", latchError.await(1500, TimeUnit.MILLISECONDS));

		// onError 経路で終了する想定なので、chunk/complete は呼ばれないこと
		assertEquals("onChunkが呼ばれた", 1L, latchChunk.getCount());
		assertEquals("onCompleteが呼ばれた", 1L, latchComplete.getCount());
		// **** 修正（2025/12/13）
		// 呼び出し元で例外を受け取れていないことを確認
		//assertFalse("例外が伝播している", thrown.get());
		// 完了は来ない
		//assertFalse("onComplete が呼ばれた", latchComplete.await(300, TimeUnit.MILLISECONDS));
		// 2025/12/13 DISCONNECT_AT_START は 接続を受けた直後にソケットを close して、レスポンス（HTTP行/ヘッダ/ボディ）を一切送らない挙動と確認できたため、不要。		
		// 先頭チャンクは届く環境が多い（届かなかったらこのアサートは外してよい）
		//		assertFalse("onChunk が呼ばれた", latchChunk.await(800, TimeUnit.MILLISECONDS));
		// **** 修正（2025/12/13）		
		//修正ソース追加分
	}
	// -----------------------------
	// IFテスト：正常系
	// -----------------------------

	/**
	 * IF-01：message → message_end の正規シーケンス
	 * 期待：合格
	 *  - onChunk が（message回数分）呼ばれる
	 *  - onComplete が1回呼ばれる
	 *  - onError は呼ばれない
	 */
	@Test
	public void testIF01_MessageToMessageEnd_Success() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"event\":\"message\",\"answer\":\"a\"}\n\n"
								+ "data: {\"event\":\"message\",\"answer\":\"b\"}\n\n"
								+ "data: {\"event\":\"message_end\"}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-01"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onCompleteが呼ばれていない", latchComplete.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onErrorが呼ばれた", 1L, latchError.getCount());
		assertTrue(
			    "message を処理した onChunk が一度も呼ばれていない",
			    chunkCalls.get() > 0
			);
		assertEquals("onCompleteが複数回呼ばれた", 0L, latchComplete.getCount());
	}

	/**
	 * IF-02：answer が空文字（message） → continue → message_end で完了
	 * 期待：合格
	 *  - 空文字チャンクでは onChunk は呼ばれない（continue）
	 *  - message_end で onComplete は呼ばれる
	 *  - onError は呼ばれない
	 */
	@Test
	public void testIF02_EmptyAnswer_Continue_ThenComplete() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"event\":\"message\",\"answer\":\"\"}\n\n"
								+ "data: {\"event\":\"message_end\"}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-02"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onCompleteが呼ばれていない", latchComplete.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onErrorが呼ばれた", 1L, latchError.getCount());
		assertEquals("空文字チャンクでonChunkが呼ばれた", 0, chunkCalls.get());
	}

	/**
	 * IF-03：message_end 単独（answer無し）
	 * 期待：合格
	 *  - onComplete が呼ばれる
	 *  - onChunk は呼ばれない
	 *  - onError は呼ばれない
	 */
	@Test
	public void testIF03_MessageEndOnly_Complete() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"event\":\"message_end\"}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-03"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onCompleteが呼ばれていない", latchComplete.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onErrorが呼ばれた", 1L, latchError.getCount());
		assertEquals("message_end単独でonChunkが呼ばれた", 0, chunkCalls.get());
	}

	// -----------------------------
	// IFテスト：異常系（アプリ層エラー）
	// -----------------------------

	/**
	 * IF-E1：アプリ層エラー（errorフィールドを含むJSON）
	 * → HTTP200でも、payloadに error が含まれていれば問答無用で onError（fail-fast）
	 * 期待：合格
	 *  - onError が呼ばれる
	 *  - onChunk は発火しない
	 *  - onComplete は発火しない
	 */
	@Test
	public void testIFE1_ErrorField_FailFastOnError() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"error\":\"Invalid API key\"}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-E1"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onErrorが呼ばれていない", latchError.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onCompleteが呼ばれた", 1L, latchComplete.getCount());
		assertEquals("onChunkが呼ばれた", 0, chunkCalls.get());
	}

	/**
	 * IF-E2：アプリ層エラー（code + message を含むJSON）
	 * → HTTP200でも、payloadに code+message が含まれていれば問答無用で onError（fail-fast）
	 * 期待：合格
	 *  - onError が呼ばれる
	 *  - onChunk は発火しない
	 *  - onComplete は発火しない
	 */
	@Test
	public void testIFE2_CodeAndMessage_FailFastOnError() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"code\":\"unauthorized\",\"message\":\"Invalid API key\"}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-E2"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onErrorが呼ばれていない", latchError.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onCompleteが呼ばれた", 1L, latchComplete.getCount());
		assertEquals("onChunkが呼ばれた", 0, chunkCalls.get());
	}

	// -----------------------------
	// IFテスト：異常系（event）
	// -----------------------------

	/**
	 * IF-04：event 欠落
	 * 期待：合格
	 *  - onError が呼ばれる
	 *  - onChunk / onComplete は呼ばれない
	 */
	@Test
	public void testIF04_EventMissing_OnError() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"answer\":\"test\"}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-04"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onErrorが呼ばれていない", latchError.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onCompleteが呼ばれた", 1L, latchComplete.getCount());
		assertEquals("onChunkが呼ばれた", 0, chunkCalls.get());
	}

	/**
	 * IF-05：event 空白 / 空白文字のみ
	 * 期待：合格
	 *  - onError が呼ばれる
	 *  - onChunk / onComplete は呼ばれない
	 */
	@Test
	public void testIF05_EventBlank_OnError() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"event\":\"   \",\"answer\":\"test\"}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-05"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onErrorが呼ばれていない", latchError.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onCompleteが呼ばれた", 1L, latchComplete.getCount());
		assertEquals("onChunkが呼ばれた", 0, chunkCalls.get());
	}

	/**
	 * IF-06：未知の event
	 * 期待：合格（※設計判断：未知eventは fail-fast で onError）
	 *  - onError が呼ばれる
	 *  - onChunk / onComplete は呼ばれない
	 *
	 */
	@Test
	public void testIF06_UnknownEvent_OnError() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"event\":\"unknown_event\",\"answer\":\"test\"}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-06"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onErrorが呼ばれていない", latchError.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onCompleteが呼ばれた", 1L, latchComplete.getCount());
		assertEquals("onChunkが呼ばれた", 0, chunkCalls.get());
	}

	// -----------------------------
	// IFテスト：異常系（answer 型）
	// -----------------------------

	/**
	 * IF-07：answer が string 以外（代表：数値）
	 * 期待：合格
	 *  - onError が呼ばれる
	 *  - onChunk / onComplete は呼ばれない
	 */
	@Test
	public void testIF07_AnswerNotString_Number_OnError() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "text/event-stream; charset=utf-8")
				.setBody(
						"data: {\"event\":\"message\",\"answer\":123}\n\n"));

		String url = mockServer.url("/chat").toString();
		DifyApiClient api = new DifyApiClient(url, "dummy");

		AtomicInteger chunkCalls = new AtomicInteger(0);
		CountDownLatch latchComplete = new CountDownLatch(1);
		CountDownLatch latchError = new CountDownLatch(1);

		api.streamingMsg(new DifyRequestDto("IF-07"),
				s -> chunkCalls.incrementAndGet(),
				() -> latchComplete.countDown(),
				e -> latchError.countDown());

		assertTrue("onErrorが呼ばれていない", latchError.await(1500, TimeUnit.MILLISECONDS));
		assertEquals("onCompleteが呼ばれた", 1L, latchComplete.getCount());
		assertEquals("onChunkが呼ばれた", 0, chunkCalls.get());
	}
}