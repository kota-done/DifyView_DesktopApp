/**
 * 
 */
package app.windowView.config;

import static java.lang.System.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class AppSettingFactoryTest {

	//テストメソッド実行用変数
	String testlog;
	String testlogFile;
	String testTitle;
	String testwindUrl;
	int testwindWidth;
	int testwindHeight;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		// パターン①：正常系　インスタンス生成が正常に生成でき、取得メソッドにて取得できる。
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン1";

		testlog = "テスト";
		testlogFile = "テスト";
		testTitle = "テスト";
		testwindUrl = "テスト";
		testwindWidth = 30;
		testwindHeight = 30;

		AppSettingFactory test1 = new AppSettingFactory();
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			test1.createInstance(testlog, testlogFile, testTitle, testwindUrl, testwindWidth, testwindHeight);
			AppSettingDto testValue = test1.getInstance();

			assertEquals("テスト", testValue.getLogPath());
			assertEquals("テスト", testValue.getLogFileExtensionString());
			assertEquals("テスト", testValue.getTitle());
			assertEquals("テスト", testValue.getWindUrl());
			assertEquals(30, testValue.getWindWidth());
			assertEquals(30, testValue.getWindHeight());

			out.println("logPass: " + testValue.getLogPath());
			out.println("logFileExtension: " + testValue.getLogFileExtensionString());
			out.println("title: " + testValue.getTitle());
			out.println("windUrl: " + testValue.getWindUrl());
			out.println("windWidth: " + testValue.getWindWidth());
			out.println("windHeight: " + testValue.getWindHeight());

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);
			e.printStackTrace();
			fail("テストパターン1失敗。");
		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//	// パターン②：異常系　createInstanceの引数にNullが入っているときエラーとなる。
	//	@Test
	//	public void test2_1() {
	//		String className = new Object() {
	//		}.getClass().getName();
	//		String resultOutopu = className + "のテストパターン2_1";
	//
	//		testlogFile = "テスト";
	//		testTitle = "テスト";
	//		testwindUrl = "テスト";
	//		testwindWidth = 30;
	//		testwindHeight = 30;
	//
	//		AppSettingFactory test2_1 = new AppSettingFactory();
	//		try {
	//			out.println("**********************************************");
	//			out.println(resultOutopu + "が開始されました。");
	//
	//			test2_1.createInstance(testlog, testlogFile, testTitle, testwindUrl, testwindWidth, testwindHeight);
	//			AppSettingDto testValue = test2_1.getInstance();
	//
	//			assertEquals("テスト", testValue.getLogPass());
	//			assertEquals("テスト", testValue.getLogFileExtensionString());
	//			assertEquals("テスト", testValue.getTitle());
	//			assertEquals("テスト", testValue.getWindUrl());
	//			assertEquals(30, testValue.getWindWidth());
	//			assertEquals(30, testValue.getWindHeight());
	//
	//			out.println("logPass: " + testValue.getLogPass());
	//			out.println("logFileExtension: " + testValue.getLogFileExtensionString());
	//			out.println("title: " + testValue.getTitle());
	//			out.println("windUrl: " + testValue.getWindUrl());
	//			out.println("windWidth: " + testValue.getWindWidth());
	//			out.println("windHeight: " + testValue.getWindHeight());
	//
	//		} catch (Exception e) {
	//			// TODO: handle exception
	//			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
	//			out.println(resultError);
	//
	//		} finally {
	//			out.println(resultOutopu + "が終了しました。");
	//			out.println("**********************************************");
	//		}
	//	}
	//
	//	// パターン②：異常系　createInstanceの引数に空白が入っているときエラーとなる。
	//	@Test
	//	public void test2_2() {
	//		String className = new Object() {
	//		}.getClass().getName();
	//		String resultOutopu = className + "のテストパターン2_2";
	//
	//		testlog = "　　";
	//		testlogFile = "";
	//		testTitle = "テスト";
	//		testwindUrl = "テスト";
	//		testwindWidth = 30;
	//		testwindHeight = 30;
	//		AppSettingFactory test2_2 = new AppSettingFactory();
	//
	//		try {
	//			out.println("**********************************************");
	//			out.println(resultOutopu + "が開始されました。");
	//
	//			test2_2.createInstance(testlog, testlogFile, testTitle, testwindUrl, testwindWidth, testwindHeight);
	//			AppSettingDto testValue = test2_2.getInstance();
	//
	//			assertEquals("テスト", testValue.getLogPass());
	//			assertEquals("テスト", testValue.getLogFileExtensionString());
	//			assertEquals("テスト", testValue.getTitle());
	//			assertEquals("テスト", testValue.getWindUrl());
	//			assertEquals(30, testValue.getWindWidth());
	//			assertEquals(30, testValue.getWindHeight());
	//
	//			out.println("logPass: " + testValue.getLogPass());
	//			out.println("logFileExtension: " + testValue.getLogFileExtensionString());
	//			out.println("title: " + testValue.getTitle());
	//			out.println("windUrl: " + testValue.getWindUrl());
	//			out.println("windWidth: " + testValue.getWindWidth());
	//			out.println("windHeight: " + testValue.getWindHeight());
	//
	//		} catch (Exception e) {
	//			// TODO: handle exception
	//			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
	//			out.println(resultError);
	//
	//		} finally {
	//			out.println(resultOutopu + "が終了しました。");
	//			out.println("**********************************************");
	//		}
	//	}

	// パターン②：異常系　createInstanceを実行せずに、getInstanceを実行してエラーとなる。
	@Test
	public void test2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutopu = className + "のテストパターン2";

		testlog = "テスト";
		testlogFile = "テスト";
		testTitle = "テスト";
		testwindUrl = "テスト";
		testwindWidth = 30;
		testwindHeight = 30;

		AppSettingFactory test2 = new AppSettingFactory();

		try {
			out.println("**********************************************");
			out.println(resultOutopu + "が開始されました。");
			AppSettingDto testValue = test2.getInstance();

			assertEquals("テスト", testValue.getLogPath());
			assertEquals("テスト", testValue.getLogFileExtensionString());
			assertEquals("テスト", testValue.getTitle());
			assertEquals("テスト", testValue.getWindUrl());
			assertEquals(30, testValue.getWindWidth());
			assertEquals(30, testValue.getWindHeight());

			out.println("logPass: " + testValue.getLogPath());
			out.println("logFileExtension: " + testValue.getLogFileExtensionString());
			out.println("title: " + testValue.getTitle());
			out.println("windUrl: " + testValue.getWindUrl());
			out.println("windWidth: " + testValue.getWindWidth());
			out.println("windHeight: " + testValue.getWindHeight());

		} catch (Exception e) {
			// TODO: handle exception
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);
		} finally {
			out.println(resultOutopu + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//　パターン③：正常系　2回生成すると上書きになる。
	@Test
	public void test3() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutopu = className + "のテストパターン3";

		AppSettingFactory test3 = new AppSettingFactory();

		try {
			out.println("**********************************************");
			out.println(resultOutopu + "が開始されました。");

			out.println("1回目が開始されました。");
			testlog = "テスト";
			testlogFile = "テスト";
			testTitle = "テスト";
			testwindUrl = "テスト";
			testwindWidth = 30;
			testwindHeight = 30;
			test3.createInstance(testlog, testlogFile, testTitle, testwindUrl, testwindWidth, testwindHeight);
			AppSettingDto testValue = test3.getInstance();

			assertEquals("テスト", testValue.getLogPath());
			assertEquals("テスト", testValue.getLogFileExtensionString());
			assertEquals("テスト", testValue.getTitle());
			assertEquals("テスト", testValue.getWindUrl());
			assertEquals(30, testValue.getWindWidth());
			assertEquals(30, testValue.getWindHeight());

			out.println("logPass: " + testValue.getLogPath());
			out.println("logFileExtension: " + testValue.getLogFileExtensionString());
			out.println("title: " + testValue.getTitle());
			out.println("windUrl: " + testValue.getWindUrl());
			out.println("windWidth: " + testValue.getWindWidth());
			out.println("windHeight: " + testValue.getWindHeight());

			out.println("2回目が開始されました。");
			testlog = "テスト2";
			testlogFile = "テスト2";
			testTitle = "テスト2";
			testwindUrl = "テスト2";
			testwindWidth = 40;
			testwindHeight = 40;
			
			test3.createInstance(testlog, testlogFile, testTitle, testwindUrl, testwindWidth, testwindHeight);
			testValue = test3.getInstance();
			assertEquals("テスト2", testValue.getLogFileExtensionString());
			assertEquals("テスト2", testValue.getTitle());
			assertEquals("テスト2", testValue.getWindUrl());
			assertEquals(40, testValue.getWindWidth());
			assertEquals(40, testValue.getWindHeight());

			out.println("logPass: " + testValue.getLogPath());
			out.println("logFileExtension: " + testValue.getLogFileExtensionString());
			out.println("title: " + testValue.getTitle());
			out.println("windUrl: " + testValue.getWindUrl());
			out.println("windWidth: " + testValue.getWindWidth());
			out.println("windHeight: " + testValue.getWindHeight());

		} catch (Exception e) {
			// TODO: handle exception
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);
		} finally {
			out.println(resultOutopu + "が終了しました。");
			out.println("**********************************************");
		}
	}

}
