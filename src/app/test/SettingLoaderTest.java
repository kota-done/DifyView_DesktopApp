/**
 * 
 */
package app.test;

import static java.lang.System.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import app.util.CommonFunction;
import app.windowView.config.AppSettingDto;
import app.windowView.config.SettingLoader;

/**
 * 
 */
public class SettingLoaderTest {

	//プロパティファイルのオブジェクト
	private Properties testProps;

	/*
	* 設定ファイルのプロパティオブジェクトの初期化メソッド。
	*/
	private void setUp() {
		try {
			//正常系のみで使用する。
			testProps = CommonFunction.load("/resources/app.properties");
		} catch (IOException e) {
			fail("設定ファイルの読み込みに失敗しました: " + e.getMessage());
		}
	}

	/**
	 *テストパターン　正常系　全ての項目が正しく設定され、DTOが正しく生成される。 
	 */
	@Test
	public void test1_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン1_1";
		//プロパティぼジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			
			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("apiUrl: " + test1.getApiUrl());
			out.println("apiKey: " + test1.getApiKey());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getApiUrl());
			assertEquals("draft.test", test1.getApiKey());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);
			fail();

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　正常系　int型の設定値の前後に半角の空白が含まれていても正常に処理を実行できる
	 */
	@Test
	public void test1_2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン1_2";
		//プロパティオジェクト初期化
		setUp();

		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」の設定値の前後に半角空白をセット。
			testProps.setProperty("WINDOW_WIDTH", " 800 ");

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("apiUrl: " + test1.getApiUrl());
			out.println("apiKey: " + test1.getApiKey());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getApiUrl());
			assertEquals("draft.test", test1.getApiKey());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);
			fail();

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　プロパティオブジェクトがNULLの場合、エラーとなる
	 */
	@Test
	public void test2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			// プロパティオブジェクトにnullセット
			testProps = null;

			AppSettingDto test2 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test2.getLogPath());
			out.println("logFileExtension: " + test2.getLogFileExtensionString());
			out.println("title: " + test2.getTitle());
			out.println("apiUrl: " + test2.getApiUrl());
			out.println("apiKey: " + test2.getApiKey());
			out.println("windWidth: " + test2.getWindWidth());
			out.println("windHeight: " + test2.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test2.getLogPath());
			assertEquals(".log", test2.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test2.getTitle());
			assertEquals("https://example.com", test2.getApiUrl());
			assertEquals("draft.test", test2.getApiKey());
			assertEquals(800, test2.getWindWidth());
			assertEquals(600, test2.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_WIDTH」がNULLの場合、エラーとなる
	 */
	@Test
	public void test3_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3_1";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」にNULLをセット。
			testProps.setProperty("WINDOW_WIDTH", null);

			AppSettingDto test3_1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test3_1.getLogPath());
			out.println("logFileExtension: " + test3_1.getLogFileExtensionString());
			out.println("title: " + test3_1.getTitle());
			out.println("apiUrl: " + test3_1.getApiUrl());
			out.println("apiKey: " + test3_1.getApiKey());
			out.println("windWidth: " + test3_1.getWindWidth());
			out.println("windHeight: " + test3_1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test3_1.getLogPath());
			assertEquals(".log", test3_1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test3_1.getTitle());
			assertEquals("https://example.com", test3_1.getApiUrl());
			assertEquals("draft.test", test3_1.getApiKey());
			assertEquals(800, test3_1.getWindWidth());
			assertEquals(600, test3_1.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_WIDTH」がNULLの場合、エラーとなる
	 */
	@Test
	public void test3_2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3_2";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」にNULLをセット。
			testProps.setProperty("WINDOW_WIDTH", null);

			AppSettingDto test3_2 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test3_2.getLogPath());
			out.println("logFileExtension: " + test3_2.getLogFileExtensionString());
			out.println("title: " + test3_2.getTitle());
			out.println("apiUrl: " + test3_2.getApiUrl());
			out.println("apiKey: " + test3_2.getApiKey());
			out.println("windWidth: " + test3_2.getWindWidth());
			out.println("windHeight: " + test3_2.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test3_2.getLogPath());
			assertEquals(".log", test3_2.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test3_2.getTitle());
			assertEquals("https://example.com", test3_2.getApiUrl());
			assertEquals("draft.test", test3_2.getApiKey());
			assertEquals(800, test3_2.getWindWidth());
			assertEquals(600, test3_2.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_WIDTH」が空白の場合、エラーとなる
	 */
	@Test
	public void test3_3() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3_3";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」に空白をセット。
			testProps.setProperty("WINDOW_WIDTH", " ");

			AppSettingDto test3_3 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test3_3.getLogPath());
			out.println("logFileExtension: " + test3_3.getLogFileExtensionString());
			out.println("title: " + test3_3.getTitle());
			out.println("apiUrl: " + test3_3.getApiUrl());
			out.println("apiKey: " + test3_3.getApiKey());
			out.println("windWidth: " + test3_3.getWindWidth());
			out.println("windHeight: " + test3_3.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test3_3.getLogPath());
			assertEquals(".log", test3_3.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test3_3.getTitle());
			assertEquals("https://example.com", test3_3.getApiUrl());
			assertEquals("draft.test", test3_3.getApiKey());
			assertEquals(800, test3_3.getWindWidth());
			assertEquals(600, test3_3.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_WIDTH」が少数の値の場合、エラーとなる
	 */
	@Test
	public void test3_4() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3_4";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」に少数の値をセット。
			testProps.setProperty("WINDOW_WIDTH", "800.5");

			AppSettingDto test3_4 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test3_4.getLogPath());
			out.println("logFileExtension: " + test3_4.getLogFileExtensionString());
			out.println("title: " + test3_4.getTitle());
			out.println("apiUrl: " + test3_4.getApiUrl());
			out.println("apiKey: " + test3_4.getApiKey());
			out.println("windWidth: " + test3_4.getWindWidth());
			out.println("windHeight: " + test3_4.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test3_4.getLogPath());
			assertEquals(".log", test3_4.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test3_4.getTitle());
			assertEquals("https://example.com", test3_4.getApiUrl());
			assertEquals("draft.test", test3_4.getApiKey());
			assertEquals(800, test3_4.getWindWidth());
			assertEquals(600, test3_4.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_WIDTH」が文字列の場合、エラーとなる
	 */
	@Test
	public void test3_5() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3_5";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」に少数の値をセット。
			testProps.setProperty("WINDOW_WIDTH", "テスト");

			AppSettingDto test3_5 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test3_5.getLogPath());
			out.println("logFileExtension: " + test3_5.getLogFileExtensionString());
			out.println("title: " + test3_5.getTitle());
			out.println("apiUrl: " + test3_5.getApiUrl());
			out.println("apiKey: " + test3_5.getApiKey());
			out.println("windWidth: " + test3_5.getWindWidth());
			out.println("windHeight: " + test3_5.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test3_5.getLogPath());
			assertEquals(".log", test3_5.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test3_5.getTitle());
			assertEquals("https://example.com", test3_5.getApiUrl());
			assertEquals("draft.test", test3_5.getApiKey());
			assertEquals(800, test3_5.getWindWidth());
			assertEquals(600, test3_5.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　正常系　「WINDOW_WIDTH」が全角数字の場合、正しく動作する。
	 */
	@Test
	public void test3_6() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3_6";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」に少数の値をセット。
			testProps.setProperty("WINDOW_WIDTH", "８００");
			out.println("セットした値" + testProps.getProperty("WINDOW_WIDTH"));

			AppSettingDto test3_6 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test3_6.getLogPath());
			out.println("logFileExtension: " + test3_6.getLogFileExtensionString());
			out.println("title: " + test3_6.getTitle());
			out.println("apiUrl: " + test3_6.getApiUrl());
			out.println("apiKey: " + test3_6.getApiKey());
			out.println("windWidth: " + test3_6.getWindWidth());
			out.println("windHeight: " + test3_6.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test3_6.getLogPath());
			assertEquals(".log", test3_6.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test3_6.getTitle());
			assertEquals("https://example.com", test3_6.getApiUrl());
			assertEquals("draft.test", test3_6.getApiKey());
			assertEquals(800, test3_6.getWindWidth());
			assertEquals(600, test3_6.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);
			fail();

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_HEIGHT」がNULLの場合、エラーとなる
	 */
	@Test
	public void test4_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン4_1";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」にNULLをセット。
			testProps.setProperty("WINDOW_HEIGHT", null);

			AppSettingDto test4_1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test4_1.getLogPath());
			out.println("logFileExtension: " + test4_1.getLogFileExtensionString());
			out.println("title: " + test4_1.getTitle());
			out.println("apiUrl: " + test4_1.getApiUrl());
			out.println("apiKey: " + test4_1.getApiKey());
			out.println("windWidth: " + test4_1.getWindWidth());
			out.println("windHeight: " + test4_1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test4_1.getLogPath());
			assertEquals(".log", test4_1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test4_1.getTitle());
			assertEquals("https://example.com", test4_1.getApiUrl());
			assertEquals("draft.test", test4_1.getApiKey());
			assertEquals(800, test4_1.getWindWidth());
			assertEquals(600, test4_1.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_HEIGHT」がNULLの場合、エラーとなる
	 */
	@Test
	public void test4_2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン4_2";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」にNULLをセット。
			testProps.setProperty("WINDOW_HEIGHT", null);

			AppSettingDto test14_2= SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test14_2.getLogPath());
			out.println("logFileExtension: " + test14_2.getLogFileExtensionString());
			out.println("title: " + test14_2.getTitle());
			out.println("apiUrl: " + test14_2.getApiUrl());
			out.println("apiKey: " + test14_2.getApiKey());
			out.println("windWidth: " + test14_2.getWindWidth());
			out.println("windHeight: " + test14_2.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test14_2.getLogPath());
			assertEquals(".log", test14_2.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test14_2.getTitle());
			assertEquals("https://example.com", test14_2.getApiUrl());
			assertEquals("draft.test", test14_2.getApiKey());
			assertEquals(800, test14_2.getWindWidth());
			assertEquals(600, test14_2.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_HEIGHT」が空白の場合、エラーとなる
	 */
	@Test
	public void test4_3() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン4_3";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」に空白をセット。
			testProps.setProperty("WINDOW_HEIGHT", " ");

			AppSettingDto test4_3 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test4_3.getLogPath());
			out.println("logFileExtension: " + test4_3.getLogFileExtensionString());
			out.println("title: " + test4_3.getTitle());
			out.println("apiUrl: " + test4_3.getApiUrl());
			out.println("apiKey: " + test4_3.getApiKey());
			out.println("windWidth: " + test4_3.getWindWidth());
			out.println("windHeight: " + test4_3.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test4_3.getLogPath());
			assertEquals(".log", test4_3.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test4_3.getTitle());
			assertEquals("https://example.com", test4_3.getApiUrl());
			assertEquals("draft.test", test4_3.getApiKey());
			assertEquals(800, test4_3.getWindWidth());
			assertEquals(600, test4_3.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_HEIGHT」が少数の値の場合、エラーとなる
	 */
	@Test
	public void test4_4() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン4_4";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」に少数の値をセット。
			testProps.setProperty("WINDOW_HEIGHT", "600.5");

			AppSettingDto test4_4 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test4_4.getLogPath());
			out.println("logFileExtension: " + test4_4.getLogFileExtensionString());
			out.println("title: " + test4_4.getTitle());
			out.println("apiUrl: " + test4_4.getApiUrl());
			out.println("apiKey: " + test4_4.getApiKey());
			out.println("windWidth: " + test4_4.getWindWidth());
			out.println("windHeight: " + test4_4.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test4_4.getLogPath());
			assertEquals(".log", test4_4.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test4_4.getTitle());
			assertEquals("https://example.com", test4_4.getApiUrl());
			assertEquals("draft.test", test4_4.getApiKey());
			assertEquals(800, test4_4.getWindWidth());
			assertEquals(600, test4_4.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「WINDOW_HEIGHT」が文字列の場合、エラーとなる
	 */
	@Test
	public void test4_5() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン4_5";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」に少数の値をセット。
			testProps.setProperty("WINDOW_HEIGHT", "テスト");

			AppSettingDto test4_5 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test4_5.getLogPath());
			out.println("logFileExtension: " + test4_5.getLogFileExtensionString());
			out.println("title: " + test4_5.getTitle());
			out.println("apiUrl: " + test4_5.getApiUrl());
			out.println("apiKey: " + test4_5.getApiKey());
			out.println("windWidth: " + test4_5.getWindWidth());
			out.println("windHeight: " + test4_5.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test4_5.getLogPath());
			assertEquals(".log", test4_5.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test4_5.getTitle());
			assertEquals("https://example.com", test4_5.getApiUrl());
			assertEquals("draft.test", test4_5.getApiKey());
			assertEquals(800, test4_5.getWindWidth());
			assertEquals(600, test4_5.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　正常系　「WINDOW_HEIGHT」が全角数字の場合、正しく動作する。
	 */
	@Test
	public void test4_6() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン4_6";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_HEIGHT」に少数の値をセット。
			testProps.setProperty("WINDOW_HEIGHT", "６００");
			out.println("セットした値" + testProps.getProperty("WINDOW_HEIGHT"));

			AppSettingDto test4_6 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test4_6.getLogPath());
			out.println("logFileExtension: " + test4_6.getLogFileExtensionString());
			out.println("title: " + test4_6.getTitle());
			out.println("apiUrl: " + test4_6.getApiUrl());
			out.println("apiKey: " + test4_6.getApiKey());
			out.println("windWidth: " + test4_6.getWindWidth());
			out.println("windHeight: " + test4_6.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test4_6.getLogPath());
			assertEquals(".log", test4_6.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test4_6.getTitle());
			assertEquals("https://example.com", test4_6.getApiUrl());
			assertEquals("draft.test", test4_6.getApiKey());
			assertEquals(800, test4_6.getWindWidth());
			assertEquals(600, test4_6.getWindHeight());


		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「logPath」「logFileExtension」「title」「apiUrl」のいずれかがnullまたは空白の場合、エラーとなる
	 */
	@Test
	public void test5() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン5";
		//プロパティオジェクト初期化
		setUp();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");

			//「WINDOW_WIDTH」に少数の値をセット。
			testProps.setProperty("LOGPATH", null);

			AppSettingDto test5 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test5.getLogPath());
			out.println("logFileExtension: " + test5.getLogFileExtensionString());
			out.println("title: " + test5.getTitle());
			out.println("apiUrl: " + test5.getApiUrl());
			out.println("apiKey: " + test5.getApiKey());
			out.println("windWidth: " + test5.getWindWidth());
			out.println("windHeight: " + test5.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test5.getLogPath());
			assertEquals(".log", test5.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test5.getTitle());
			assertEquals("https://example.com", test5.getApiUrl());
			assertEquals("draft.test", test5.getApiKey());
			assertEquals(800, test5.getWindWidth());
			assertEquals(600, test5.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("正しくエラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

}
