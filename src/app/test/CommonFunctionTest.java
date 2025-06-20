package app.test;

import static java.lang.System.*;

import org.junit.Test;

import app.util.CommonFunction;

public class CommonFunctionTest {

	//テストパターン　正常系　引数一つ
	@Test
	public void test1_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン1_1";

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
		String resultOutput = className + "のテストパターン1_2";

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
	public void test2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2";

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
	public void test3_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3_1";

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
	public void test3_2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン3_2";

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
	public void test4() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン4";

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

}
