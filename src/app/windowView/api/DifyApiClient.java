package app.windowView.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import app.util.CommonFunction;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * API通信処理実装クラス
 * @author 
 * @version 1.1
 * 修正：
 * 7/26　最終チャンク前に空文字のチャンクが混入する仕様になっていたため、チェック処理追加。
 */
public class DifyApiClient {
	private final String difyAPI_URL;
	private final String apiKey;
	private final OkHttpClient httpClient;
	private final Gson gson = new Gson();
	//ロガーオブジェクト
	private static final Logger logger = LoggerFactory.getLogger(DifyApiClient.class);

	/**
	*引数付きコンストラクタ
	* @param apiUrl　API通信するURL  
	* @param apiKey　API通信のキー
	*/
	public DifyApiClient(String apiUrl, String apiKey) {
		this.difyAPI_URL = apiUrl;
		this.apiKey = apiKey;
		this.httpClient = new OkHttpClient();
	}

	/**
	*DifyAPIとのストリーミング通信処理
	* @param dto DifyRequestDtoオブジェクト：リクエスト
	* @param onChunk レスポンス成功時に実行するメソッド
	* @param conComplete レスポンス受信終了時に実行するメソッド
	*/
	public void streamingMsg(DifyRequestDto dto, Consumer<String> onChunk, Runnable onComplete) {
		//リクエストの中身
		RequestBody body = RequestBody.create(
				//送信データのメディアタイプををJson形式に設定。引数はテンプレ。
				MediaType.parse("application/json"),
				gson.toJson(dto).getBytes(StandardCharsets.UTF_8));

		//リクエストのヘッダ+中身を融合
		Request request = new Request.Builder()
				.url(difyAPI_URL)
				.header("Authorization", "Bearer " + apiKey)
				.post(body)
				.build();
		//System.out.println(request);
		//
		httpClient.newCall(request).enqueue(new Callback() {
			//通信失敗時の処理
			@Override
			public void onFailure(Call call, IOException e) {
				logger.error("API通信：URLエラー url={}", difyAPI_URL, e);
				throw new IllegalStateException("APIエラー:" + e.getMessage());
			}

			//通信が成功して何かしらのレスポンスを受信した時
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// ステータスコードチェック（200系以外はNG）
				if (!response.isSuccessful()) {
					logger.error("API通信：通信ステータスエラー code{}",response.code());
					throw new IOException("HTTPエラー；" + response.code());
				}
				//レスポンスのnullチェック
				ResponseBody responseBody = response.body();
				if (responseBody == null) {
					logger.error("API通信：レスポンスnull");
					throw new IllegalStateException("レスポンスの中身がnullです。");
				}

				//チャンク毎にレスポンスの受け取り
				try (BufferedSource source = responseBody.source()) {

					while (!source.exhausted()) {
						//1行ずつUTF-8形式で格納
						String resline = source.readUtf8LineStrict();

						if (resline != null && resline.startsWith("data:")) {

							String data = resline.substring(6);

							DifyResponseDto chunk = gson.fromJson(data, DifyResponseDto.class);
							//チャンクのawnser取り出し
							String answer = chunk.getAnswer();
							//空文字の場合（message_endの一つ手前）、スキップ。 7/26動作確認時点で受信チャンクに混入していたため追加。
							if (answer != null && answer.trim().isEmpty()) {
								continue;
							}
							//受信チャンクのイベントをチェックし、終了イベントなら処理終了。
							String event = chunk.getEvent();
							//nullでないなら、event.trim()、nullなら空文字を返す。
							if ("message_end".equals(event != null ? event.trim() : "")) {
								onComplete.run();
								//System.out.println("チャンクの終了を確認");
								break;
							}
							//チャンクの中身の空白チェック,空白なら例外
							CommonFunction.checkNullBlank(answer);
							//メソッド引数のonChunkにセットされているメソッドの呼び出し。
							onChunk.accept(answer);
						}
					}
				} catch (Exception e2) {
					//ロガーにエラーログ出力
					logger.error("API通信：異常終了",e2);
					//チャンク読み込み中エラー
					throw new IllegalStateException("チャンク読み込みエラー：" + e2.getMessage());
				}
			}
		});
	}

}
