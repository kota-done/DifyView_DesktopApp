package app.util;

import static java.lang.System.*;
import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class CommonFunctionTest {

	/**
	 * checkNullBlank
	 * 変数のNull、空白チェック処理のテスト
	 */
	//テストパターン　正常系　引数一つ
	@Test
	public void test1_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のcheckNullBlankメソッドのテストパターン1_1";

		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			CommonFunction.checkNullBlank("テスト");
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			// TODO: handle exception
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//テストパターン　正常系　引数複数 *10
	@Test
	public void test1_2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のcheckNullBlankメソッドのテストパターン1_2";

		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			CommonFunction.checkNullBlank("テスト", "テスト", "テスト", "テスト", "テスト", "テスト", "テスト", "テスト", "テスト", "テスト");
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			// TODO: handle exception
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//テストパターン　Nullを最初に含む。
	@Test
	public void test1_3() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のcheckNullBlankメソッドのテストパターン1_3";

		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			CommonFunction.checkNullBlank(null, "テスト", "テスト", "テスト");
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			// TODO: handle exception
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//テストパターン　空白（半角）を最初に含む。
	@Test
	public void test1_4() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のcheckNullBlankメソッドのテストパターン1_4";

		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			CommonFunction.checkNullBlank(" ", "テスト", "テスト", "テスト");
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			// TODO: handle exception
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//テストパターン　空白（全角）を最初に含む。
	@Test
	public void test1_5() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のcheckNullBlankメソッドのテストパターン1_5";

		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			CommonFunction.checkNullBlank("　", "テスト", "テスト", "テスト");
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			// TODO: handle exception
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//テストパターン　Nullまたは空白を途中に含む。
	@Test
	public void test1_6() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のcheckNullBlankメソッドのテストパターン1_6";

		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			CommonFunction.checkNullBlank("テスト", null, "テスト", "テスト");
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			// TODO: handle exception
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);

		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	/**
	 * 設定ファイル読み込み処理のテスト
	 */
	//テストパターン　引数のファイル名が正しく認識でき、Propertiesオブジェクトを返す。
	@Test
	public void test2_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のloadメソッドのテストパターン2";
		String testFile = "/resources/app.properties";
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			Properties testProp = CommonFunction.load(testFile);
			if (testProp == null) {
				throw new IllegalStateException("load処理は実行されましたが、中身は存在しません。");
			}
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);
			fail("テストパターン1失敗。");
		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//テストパターン　引数のファイル名が存在しないため、エラーとなる。
	@Test
	public void test2_2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のloadメソッドのテストパターン2";
		String testFile = ".testProperties";
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			Properties testProp = CommonFunction.load(testFile);
			if (testProp == null) {
				throw new IllegalStateException("load処理は実行されましたが、中身は存在しません。");
			}
			out.println(resultOutput + "が正常終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			out.println(resultError);
		} finally {
			out.println(resultOutput + "が終了しました。");
			out.println("**********************************************");
		}
	}

	//テストパターン　引数のファイル名がNullのため、エラーとなる。
	@Test
	public void test2_3() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のloadメソッドのテストパターン2";
		String testFile = null;
		try {
			out.println("**********************************************");
			out.println(resultOutput + "が開始されました。");
			Properties testProp = CommonFunction.load(testFile);
			if (testProp == null) {
				throw new IllegalStateException("load処理は実行されましたが、中身は存在しません。");
			}
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
	 * escapeForJS
	 * エスケープ処理
	 */
	@Test
	public void testEscapeForJS() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のtestEscapeForJSメソッドのテスト";
		out.println("**********************************************");
		out.println(resultOutput + "が開始されました。");
		try {
			//通常文字列　
			String abc = CommonFunction.escapeForJS("abc");
			System.out.println("パターン1.abc:"+abc);
			//バックスラッシュ1つ
			String abc2 = CommonFunction.escapeForJS("a\"b\"c");
			System.out.println("パターン2.abc2:"+abc2);
			//シングルクォーテーション
			String abc3 = CommonFunction.escapeForJS("a'b'c");
			System.out.println("パターン3.abc3:"+abc3);
			//改行・CR
			String abc4 = CommonFunction.escapeForJS("a\r\nb");
			System.out.println("パターン4.abc4:"+abc4);
			//タブ
			String abc5 = CommonFunction.escapeForJS("a\tb");
			System.out.println("パターン4.abc5:"+abc5);
			//改行＋文字列
			String line = CommonFunction.escapeForJS("line1\nline2");
			System.out.println("パターン5.line:"+line);
			//バックスラッシュ2つ
			String test = CommonFunction.escapeForJS("\\\\test");
			System.out.println("パターン6.test:"+test);
			//日本語（全角）
			String hello = CommonFunction.escapeForJS("こんにちは？");
			System.out.println("パターン7.hello:"+hello);
			
			assertEquals("\"abc\"", abc);
			assertEquals("\"a\\\"b\\\"c\"",abc2 );
			assertEquals("\"a\\'b\\'c\"", abc3);
			assertEquals("\"a\\r\\nb\"", abc4);
			assertEquals("\"a\\tb\"", abc5);  
			assertEquals("\"line1\\nline2\"", line);
			assertEquals("\"\\\\\\\\test\"", test);
			assertEquals("\"こんにちは？\"", hello); // 全角記号
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

}
