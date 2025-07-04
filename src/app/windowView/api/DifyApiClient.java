package app.windowView.api;

import okhttp3.OkHttpClient;

public class DifyApiClient {
	private final String difyAPI_URL;
	private final String apiKey;
	private final OkHttpClient httpClient;
	
	 /**
	 *引数付きコンストラクタ
	 * @param 1:apiUrl　API通信するURL  2:apiKey　API通信のキー
	 */
	public DifyApiClient(String apiUrl,String apiKey) {
		this.difyAPI_URL = apiUrl;
		this.apiKey = apiKey;
		this.httpClient = new OkHttpClient();
	}

}
