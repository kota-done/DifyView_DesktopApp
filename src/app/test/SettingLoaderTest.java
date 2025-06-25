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
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

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
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 *テストパターン　異常系　「logPath」「logFileExtension」「title」「windUrl」のいずれかがnullまたは空白の場合、エラーとなる
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

			AppSettingDto test1 = SettingLoader.startSettings(testProps);

			// プロパティオブジェクトの内容チェック
			out.println("logPath: " + test1.getLogPath());
			out.println("logFileExtension: " + test1.getLogFileExtensionString());
			out.println("title: " + test1.getTitle());
			out.println("windUrl: " + test1.getWindUrl());
			out.println("windWidth: " + test1.getWindWidth());
			out.println("windHeight: " + test1.getWindHeight());

			//　app.propertiesの中身と同等かチェック。
			assertEquals("testLogPath", test1.getLogPath());
			assertEquals(".log", test1.getLogFileExtensionString());
			assertEquals("chatbot_Dify", test1.getTitle());
			assertEquals("https://example.com", test1.getWindUrl());
			assertEquals(800, test1.getWindWidth());
			assertEquals(600, test1.getWindHeight());

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
