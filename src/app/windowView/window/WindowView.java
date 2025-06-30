package app.windowView.window;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * デスクトップアプリのウィンドウ表示クラス。
 * @author 
 * @version 1.0
 */
public class WindowView extends Application {
	
	
	//
	WebView webView = new WebView();

	/************
	* メソッド名：ウェブオブジェクト取得
	* 処理内容：ウェブオブジェクトを取り出す。
	* @return webView 表示したウィンドウの表示内容をもつオブジェクト
	* @throws IllegalStateException　ウェブオブジェクトがNULLの場合、例外。
	/************/
	public WebView getView() {
		if(webView == null) {
			throw new IllegalStateException("webViewが初期化されていない。");
		}
		return webView;
	}
	
	/************
	* メソッド名：ウィンドウ表示処理
	* 処理内容：JavaFXのApplicationクラスの抽象メソッドの実装。ウィンドウの設定値をセットする。
	* @param stage 呼び出し元のlaunchメソッドの第2引数、可変長のString型。
	* @return void
	* @throws IllegalStateException 実行時の引数のうち整数の項目が整数でなかった場合例外。
	/************/
	@Override
	public void start(Stage stage) throws Exception {
		


		//呼び出しメソッドlaunchの引数受け取り(1,title 2,windUrl 3,windWidth 4,windHeight)全てStrin型
		List<String> laParams = getParameters().getRaw();

		//引数の中身が存在しない場合エラー
		if (laParams.size() == 0) {
			throw new IllegalStateException("引数に必要な項目がありません。");
		}
		String title = laParams.get(0);
		String difyUrl = laParams.get(1);
		int width, height;

		//整数に格納
		try {
			width = Integer.parseInt(laParams.get(2));
			height = Integer.parseInt(laParams.get(3));

		} catch (NumberFormatException e) {
			//整数以外例外
			throw new IllegalStateException("幅または高さが整数ではありません。:" + e.getMessage());
		}
		String html = """
				          <!DOCTYPE html>
				          <html lang="ja">
				          <head>
				            <meta charset="UTF-8">
				            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
				            <title>チャットUI</title>
				            <style>
				              body {
				                font-family: sans-serif;
				                margin: 0;
				                padding: 0;
				                height: 100vh;
				                width: 100vw;
				                display: flex;
				                flex-direction: column;
				              }
				              #chat-area {
				                flex: 1;
				                padding: 10px;
				                overflow-y: auto;
				                background: #f0f0f0;
				                display: flex;
				                flex-direction: column;
				              }
				              .chat-wrapper {
				                display: flex;
				                margin: 5px 0;
				              }
				              .chat-wrapper.user {
				                justify-content: flex-end;
				              }
				              .chat-wrapper.bot {
				                justify-content: flex-start;
				              }
				              .message {
				                display: inline-block;
				                max-width: 75%;
				                background: #eee;
				                padding: 8px 12px;
				                border-radius: 10px;
				                word-break: break-word;
				                white-space: pre-wrap;
				              }
				              .message.user {
				                background: #cce5ff;
				                text-align: right;
				              }
				              .message.bot {
				                background: #d4edda;
				                text-align: left;
				              }
				              #input-area {
				                display: flex;
				                padding: 10px;
				                background: #ddd;
				                width: 100%;
				                box-sizing: border-box;
				              }
				              #user-input {
				                flex: 1;
				                padding: 8px;
				                font-size: 1em;
				                resize: none;
				                min-height: 2em;
				                max-height: 8em;
				                overflow-y: auto;
				                box-sizing: border-box;
				                white-space: pre-wrap;
				                word-break: break-word;
				                width: 100%;
				                max-width: 100%;
				              }
				              #input-area button {
				                padding: 8px 16px;
				                font-size: 1em;
				                margin-left: 5px;
				              }
				            </style>
				          </head>
				          <body>
				            <div id="chat-area">
				              <div class="chat-wrapper bot">
				                <div class="message bot">こんにちは！ご用件をどうぞ。</div>
				              </div>
				            </div>
				            <div id="input-area">
				              <textarea id="user-input" placeholder="メッセージを入力..."></textarea>
				              <button onclick="sendMessage()">送信</button>
				            </div>
				            <script>
				              const input = document.getElementById("user-input");
				              const chatArea = document.getElementById("chat-area");

				              input.addEventListener("input", () => {
				                input.style.height = "auto";
				                input.style.height = input.scrollHeight + "px";
				              });

				              function sendMessage() {
				                const message = input.value.trim();
				                if (!message) return;

				                const userWrapper = document.createElement("div");
				                userWrapper.className = "chat-wrapper user";
				                const userMsg = document.createElement("div");
				                userMsg.className = "message user";
				                userMsg.textContent = message;
				                userWrapper.appendChild(userMsg);
				                chatArea.appendChild(userWrapper);

				                const botWrapper = document.createElement("div");
				                botWrapper.className = "chat-wrapper bot";
				                const botMsg = document.createElement("div");
				                botMsg.className = "message bot";
				                botMsg.textContent = "（Botの応答）";
				                botWrapper.appendChild(botMsg);
				                chatArea.appendChild(botWrapper);

				                input.value = "";
				                input.style.height = "auto";
				                chatArea.scrollTop = chatArea.scrollHeight;
				              }
				            </script>
				          </body>
				          </html>
				""";
		//HTML読み込み
		webView.getEngine().loadContent(html);
		;

		//ウィンドウ表示
		Scene scene = new Scene(webView, width, height);
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}
}
