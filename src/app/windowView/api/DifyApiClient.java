package app.windowView.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class DifyApiClient {
	private final String difyAPI_URL;
	private final String apiKey;
	private final OkHttpClient httpClient;
	private final Gson gson = new Gson();

	/**
	*引数付きコンストラクタ
	* @param 1:apiUrl　API通信するURL  2:apiKey　API通信のキー
	*/
	public DifyApiClient(String apiUrl, String apiKey) {
		this.difyAPI_URL = apiUrl;
		this.apiKey = apiKey;
		this.httpClient = new OkHttpClient();
	}

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
				throw new IllegalStateException("APIエラー:" + e.getMessage());
			}

			//通信が成功して何かしらのレスポンスを受信した時
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// ステータスコードチェック（200系以外はNG）
				if (!response.isSuccessful()) {
					throw new IOException("API通信ステータスエラー；" + response.code());
				}
				//レスポンスのnullチェック
				ResponseBody responseBdoy = response.body();
				if (responseBdoy == null) {
					throw new IllegalStateException("レスポンスの中身がnullです。");
				}

				//チャンク毎にレスポンスの受け取り
				try (BufferedSource source = responseBdoy.source()) {

					while (!source.exhausted()) {
						//1行ずつUTF-8形式で格納
						String resline = source.readUtf8LineStrict();

						if (resline != null && resline.startsWith("data:")) {

							String data = resline.substring(6);
							//受信チャンクが終了文かどうかチェック
							//							if ("[DONE]".equals(data)) {
							//								onComplete.run();
							//								break;
							//							}

							DifyResponseDto chunk = gson.fromJson(data, DifyResponseDto.class);
							
							//受信チャンクのイベントをチェックし、終了イベントなら処理終了。
							String event = chunk.getEvent();
							//nullでないなら、event.trim()、nullなら空文字を返す。
							if("message_end".equals(event!= null ? event.trim():"")) {
								onComplete.run();
								System.out.println("チャンクの終了を確認");
								break;
							}
							//メソッド引数のonChunkにセットされているメソッドの呼び出し。
							onChunk.accept(chunk.getAnswer());
						}
					}
				} catch (Exception e2) {
					//チャンク読み込み中エラー
					throw new IllegalStateException("チャンク読み込みエラー：" + e2.getMessage());
				}
			}

		});

	}

}
