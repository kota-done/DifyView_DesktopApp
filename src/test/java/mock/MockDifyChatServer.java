package mock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * DifyチャットAPI再現用モックサーバ
 * システムテスト用にDifyAPIの挙動を簡易的に再現する。
 *
 * URLパスに応じてレスポンスを切り替える。
 *
 * /normal       : 正常SSEレスポンス
 * /http500      : HTTP500エラー
 * /sse-no-end   : SSE異常（message_end無し）
 * /slow-normal  : 遅延付き正常SSEレスポンス
 *
 * ポートは固定で 18081 を使用する。
 *
 * @author
 * @version 1.2
 * 修正：
 * 受信リクエスト内容の標準出力ログを追加
 * 遅延付き正常レスポンス（/slow-normal）を追加
 */
public class MockDifyChatServer {

    /** モックサーバの待受ポート */
    private static final int SERVER_PORT = 18081;

    /** ログ出力用日時フォーマット */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * モックサーバ起動メソッド
     *
     * Dispatcherを利用して、リクエストURLに応じて
     * レスポンスを切り替える。
     *
     * @param args 起動引数（未使用）
     */
    public static void main(String[] args) {

        MockWebServer server = new MockWebServer();

        try {
            Dispatcher dispatcher = new Dispatcher() {

                @Override
                public MockResponse dispatch(RecordedRequest request) {

                    outputRequestLog(request);

                    String path = request.getPath();

                    if (path != null && path.contains("/slow-normal")) {
                        return slowNormalResponse();
                    }

                    if (path != null && path.contains("/normal")) {
                        return normalResponse();
                    }

                    if (path != null && path.contains("/http500")) {
                        return http500Response();
                    }

                    if (path != null && path.contains("/sse-no-end")) {
                        return sseErrorResponse();
                    }

                    return new MockResponse()
                            .setResponseCode(404)
                            .setBody("Not Found");
                }
            };

            server.setDispatcher(dispatcher);
            server.start(SERVER_PORT);

            System.out.println("Mock Dify Chat Server started");
            System.out.println("url=http://localhost:" + SERVER_PORT);

            // 停止指示があるまで待機
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Mock Server 起動エラー");
            e.printStackTrace();

        } finally {
            try {
                server.shutdown();
            } catch (Exception e) {
                System.err.println("Mock Server 終了エラー");
                e.printStackTrace();
            }
        }
    }

    /**
     * 受信したリクエスト内容を標準出力する。
     *
     * @param request 受信リクエスト
     */
    private static void outputRequestLog(RecordedRequest request) {

        String now = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String method = request.getMethod();
        String path = request.getPath();
        String body = request.getBody().readUtf8();

        System.out.println("===== Mock request received =====");
        System.out.println("time   : " + now);
        System.out.println("method : " + method);
        System.out.println("path   : " + path);
        System.out.println("body   : " + body);
        System.out.println("================================");
    }

    /**
     * 正常SSEレスポンスを返す。
     *
     * @return 正常系のMockResponse
     */
    private static MockResponse normalResponse() {

        String body =
                "data: {\"event\":\"message\",\"answer\":\"これは\"}\n\n" +
                "data: {\"event\":\"message\",\"answer\":\"テスト\"}\n\n" +
                "data: {\"event\":\"message\",\"answer\":\"です\"}\n\n" +
                "data: {\"event\":\"message_end\"}\n\n";

        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "text/event-stream")
                .setBody(body);
    }

    /**
     * 遅延付き正常SSEレスポンスを返す。
     * 通信中にウィンドウを閉じるテスト用。
     *
     * @return 遅延付き正常系のMockResponse
     */
    private static MockResponse slowNormalResponse() {

        String body =
                "data: {\"event\":\"message\",\"answer\":\"これは\"}\n\n" +
                "data: {\"event\":\"message\",\"answer\":\"少し\"}\n\n" +
                "data: {\"event\":\"message\",\"answer\":\"遅い\"}\n\n" +
                "data: {\"event\":\"message_end\"}\n\n";

        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "text/event-stream")
                .setBody(body)
                .setBodyDelay(3, TimeUnit.SECONDS);
    }

    /**
     * HTTP500レスポンスを返す。
     *
     * @return HTTP500系のMockResponse
     */
    private static MockResponse http500Response() {

        return new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error");
    }

    /**
     * SSE異常レスポンス（message_end無し）を返す。
     *
     * @return SSE異常系のMockResponse
     */
    private static MockResponse sseErrorResponse() {

        String body =
                "data: {\"event\":\"message\",\"answer\":\"これは\"}\n\n" +
                "data: {\"event\":\"message\",\"answer\":\"途中\"}\n\n";

        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "text/event-stream")
                .setBody(body);
    }
}