package app.test;

import static java.lang.System.*;

import app.windowView.window.WindowView;
import javafx.application.Application;

public class WindowViewTest {

	public static void main(String[] args) {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン1";

		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			
			String testTitle = "テスト";
//			String testUrl = "https://google.com";
			String testWidth = "800";
			String testHeight = "600";
			
			//ウィンドウ表示実行。
			Application.launch(WindowView.class,testTitle,testWidth,testHeight);
			
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}

	}
}
