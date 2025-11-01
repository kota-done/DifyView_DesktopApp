package app.test;


import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import app.windowView.api.DifyApiClient;
import app.windowView.api.DifyRequestDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * DifyApiClientのエラー挙動確認テストクラス
 * - 通信エラーやHTTP500、JSON内エラーが呼び出し元に伝播しないことを確認

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
     */
    @Test
    public void testHttp500() throws IOException, InterruptedException {
        // モックレスポンスを登録
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"message\":\"server error\"}"));

        String url = mockServer.url("/chat").toString();
        DifyApiClient api = new DifyApiClient(url, "dummy");

        // コールバック制御
        CountDownLatch latchChunk = new CountDownLatch(1);
        CountDownLatch latchComplete = new CountDownLatch(1);
        AtomicBoolean thrown = new AtomicBoolean(false);

        try {
            api.streamingMsg(new DifyRequestDto("テストパターン1"),
                    s -> latchChunk.countDown(),
                    () -> latchComplete.countDown());
        } catch (Throwable t) {
            thrown.set(true);
        }

        // 呼び出し元で例外を受け取れていないことを確認
        assertFalse("例外が伝播している", thrown.get());
        // コールバックが発火しないこと
        assertFalse("onChunkが呼ばれた", latchChunk.await(300, TimeUnit.MILLISECONDS));
        assertFalse("onCompleteが呼ばれた", latchComplete.await(300, TimeUnit.MILLISECONDS));
    }

    /**
     * 正常系確認に通信するが処理を中断して、チャンク処理中に例外発生。
     */
    @Test
    public void testChunkEnd() throws IOException, InterruptedException {
        String sseData =
                "data: {\"event\":\"message\",\"answer\":\"test\"}\n\n" +
                "data: {\"event\":\"message_end\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "text/event-stream")
                .setBody(sseData));

        String url = mockServer.url("/chat").toString();
        DifyApiClient api = new DifyApiClient(url, "dummy");

        CountDownLatch latchChunk = new CountDownLatch(1);
        CountDownLatch latchComplete = new CountDownLatch(1);

        api.streamingMsg(new DifyRequestDto("テストパターン2"),
                s -> latchChunk.countDown(),
                () -> latchComplete.countDown());	

        assertTrue("onChunkが呼ばれていない", latchChunk.await(500, TimeUnit.MILLISECONDS));
        assertTrue("onCompleteが呼ばれていない", latchComplete.await(500, TimeUnit.MILLISECONDS));
    }
}