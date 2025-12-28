package app.windowView.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
 * @version 1.2
 * 修正：
 * 2025/7/26　最終チャンク前に空文字のチャンクが混入する仕様になっていたため、チェック処理追加。
 * 2025/11/3　呼び出し元で例外発生をキャッチできないため、ストリーミング処理固有のエラーレスポンスを実装。
 * 2025/12/20 event欠落、answerの文字列以外の例外スローを追加。
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
	* @param onChunk レスポンス成功時に実行するメソッド（呼び出し元で詳細を実装）
	* @param conComplete レスポンス受信終了時に実行するメソッド（呼び出し元で詳細を実装）
	* @param onError エラーが発生した時に実行するメソッド（呼び出し元で詳細を実装）
	*/
	public void streamingMsg(
			DifyRequestDto dto,
			Consumer<String> onChunk,
			Runnable onComplete,
			Consumer<String> onError) {
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

		httpClient.newCall(request).enqueue(new Callback() {
			//通信失敗時の処理
			@Override
			public void onFailure(Call call, IOException e) {
				logger.error("API通信：URLエラー url={}", difyAPI_URL, e);
				//20251102上位にスローできないため廃止
				//throw new IllegalStateException("APIエラー:" + e.getMessage());
				onError.accept("AIとの通信に失敗しました。アプリを再起動してください。");
			}

			//通信が成功して何かしらのレスポンスを受信した時
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// ステータスコードチェック（200系以外はNG）
				if (!response.isSuccessful()) {
					logger.error("API通信：通信ステータスエラー code{}", response.code());
					//20251102上位にスローできないため廃止
					//throw new IOException("HTTPエラー；" + response.code());
					onError.accept("通信エラーが発生しました。時間をおいて再試行してください。");
					return;
				}
				//レスポンスのnullチェック
				ResponseBody responseBody = response.body();
				if (responseBody == null) {
					logger.error("API通信：レスポンスnull");
					//20251102上位にスローできないため廃止
					//throw new IllegalStateException("レスポンスの中身がnullです。");
					onError.accept("AIからのレスポンスの中身がないためエラーとなりました。時間をおいて再試行してください。");
					return;
				}

				//チャンク毎にレスポンスの受け取り
				try (BufferedSource source = responseBody.source()) {

					while (!source.exhausted()) {
						//1行ずつUTF-8形式で格納
						String resline = source.readUtf8LineStrict();

						if (resline != null && resline.startsWith("data:")) {

							//5文字以降をtrim
							String data = resline.substring(5).trim();
							//20251102 Jsonオブジェクト内のエラーメッセジーをチェック。　start
							// レスポンスのJsonオブジェクト 
							JsonObject root = gson.fromJson(data, JsonObject.class);
							if (root == null) {
								logger.error("API通信：JSONパース結果がnull。data={}", data);
								continue; // ← rootがnullなら以降の処理をスキップ
							}
							// rootの内容をもとにエラー判定
							if (root.has("error") || (root.has("code") && root.has("message"))) {
								logger.error("API通信：アプリ層エラー payload={}", data);
								onError.accept("処理に失敗しました。時間をおいて再試行してください。");
								return;
							}
							//**20251220 eventの欠落、空白時の除外
							if (!root.has("event")
									|| root.get("event").isJsonNull()
									|| root.get("event").getAsString().trim().isEmpty()) {

								logger.error("API通信：event欠落または空 payload={}", data);
								onError.accept("AIからのレスポンス形式に異常がでました。再度実行してください。");
								return;
							}
							//20251220 **

							//20251102 Jsonオブジェクト内のエラーメッセジーをチェック。 finish
							//受信チャンクのイベントをチェックし、終了イベントなら処理終了。
							//20251220 Dtoオブジェクトへの変換前にチェックすることで、変換部分をスキップ**
							String event = root.get("event").getAsString().trim();
							if ("message_end".equals(event)) {
								onComplete.run();
								break;
							}
							// 20251220**
							//							String event = chunk.getEvent();
							//							if ("message_end".equals(event.trim())) {
							//								onComplete.run();
							//								break;
							//							}
							// message以外はIF違反として落とす（or continueにするならここを変更）
							if (!"message".equals(event)) {
								logger.error("API通信：event未知/非対応 event={} payload={}", event, data);
								onError.accept("AIからのレスポンス形式が不正です。再度実行してください。");
								return;
							}

							if ("message".equals(event)) {
								if (!root.has("answer") || root.get("answer").isJsonNull()) {
									logger.error("API通信：answer欠落/null payload={}", data);
									onError.accept("AIからのレスポンス形式が不正です。再度実行してください。");
									return;
								}
								if (!root.get("answer").isJsonPrimitive()
										|| !root.get("answer").getAsJsonPrimitive().isString()) {
									logger.error("API通信：answer型不正（string以外） payload={}", data);
									onError.accept("AIからのレスポンス形式が不正です。再度実行してください。");
									return;
								}
							}
							DifyResponseDto chunk = gson.fromJson(data, DifyResponseDto.class);
							if (chunk == null) {
								logger.error("API通信：DTO変換結果がnull。data={}", data);
								continue;
							}
							//チャンクのawnser取り出し
							String answer = chunk.getAnswer();
							//空文字の場合（message_endの一つ手前）、スキップ。 7/26動作確認時点で受信チャンクに混入していたため追加。
							if (answer != null && answer.trim().isEmpty()) {
								continue;
							}
							onChunk.accept(answer);
						}
					}
				} catch (Exception e2) {
					//ロガーにエラーログ出力
					logger.error("API通信：異常終了", e2);
					//20251102上位にスローできないため廃止
					//チャンク読み込み中エラー
					//throw new IllegalStateException("チャンク読み込みエラー：" + e2.getMessage());
					onError.accept("AIからのレスポンス処理で問題が発生しました。再度実行してください。");
				}
			}
		});
	}

}
