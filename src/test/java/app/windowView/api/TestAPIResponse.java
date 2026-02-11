package app.windowView.api;

import java.util.List;
import java.util.function.Consumer;

import com.google.gson.Gson;

import app.util.CommonFunction;
import app.windowView.api.DifyResponseDto;

public class TestAPIResponse {
	//
	private Gson gson = new Gson();
	/**
	*テスト用のレスポンス受信後処理
	* @param response テスト用で設定したレスポンスJSON
	* @param onChunk　　チャンク受信ができている時の処理
	* @param onComplete　終了メッセージがあった時の処理
	*/
	public void handleResponse(List<String> testLines, Consumer<String> onChunk, Runnable onComplete) {
		//１行ずつ処理

		for (String resline : testLines) {
//			System.out.println("handleResponse() に入りました");
//			System.out.println("受信行: " + resline);
			try {
				if (resline != null && resline.startsWith("data:")) {
					String data = resline.substring(6).trim();
					DifyResponseDto chunk = gson.fromJson(data, DifyResponseDto.class);

					//チャンクの中身の空白チェック,空白なら例外
					CommonFunction.checkNullBlank(chunk.getAnswer());
					String event = chunk.getEvent();
					if ("message_end".equals(event != null ? event.trim() : "")) {
						onComplete.run();
						break;
					}
					onChunk.accept(chunk.getAnswer().trim());
				}
			}catch(Exception e) {
				throw new IllegalStateException("チャンク読み込みエラー：" + e.getMessage());
			}
		}
	}
}
