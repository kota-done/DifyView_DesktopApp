/**
 * 
 */
package app.windowView.api;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.google.gson.Gson;

/**
 * 
 */
public class DifyApiClientTest {

	Gson gson = new Gson();

	//テストメソッドクラスのインスタンス
	TestAPIResponse testApi = new TestAPIResponse();

	/**
	 *テストパターン　正常系　チャンク毎に受信でき、最終チャンクで正しく処理が終了する。
	 */
	@Test
	public void test1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン1";

		//レスポンス処理の処理結果フラグ
		AtomicBoolean isComplete = new AtomicBoolean(false);

		//テスト用のレスポンスJSONリスト。
		List<String> testJSONList = List.of(
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"これは\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"テストの\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"レスポンス\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"です。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"次で\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"おわり。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message_end\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"終わりませんでした\",\"from_variable_selector\":null}");
		List<String> testReults = new ArrayList<>();
		try {
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が開始されました。");

			testApi.handleResponse(testJSONList, chunk -> {
				testReults.add(chunk);
			}, () -> {
				//event:message_endを認識できた場合、チャンク受信を完了。
				isComplete.set(true);
				System.out.println("受信したチャンクの中身；" + testReults);
			});

			//アサート
			List<String> assertResult = List.of("これは", "テストの", "レスポンス", "です。", "次で", "おわり。");
			assertEquals(assertResult, testReults);
			//フラグチェック
			assertTrue("最終チャンクの受信を認識できていません。", isComplete.get());
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が正常に終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			System.out.println(resultError);
			fail("正常系処理に失敗");
		}

	}

	/**
	 *テストパターン　異常系　最終チャンクに「event：message_end」がないため、タイムアウトでエラーとなる。
	 */
	@Test
	public void test2_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2_1";

		//レスポンス処理の処理結果フラグ
		AtomicBoolean isComplete = new AtomicBoolean(false);

		//テスト用のレスポンスJSONリスト。
		List<String> testJSONList = List.of(
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"これは\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"テストの\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"レスポンス\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"です。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"次で\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"おわり。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"終わりませんでした\",\"from_variable_selector\":null}");
		List<String> testReults = new ArrayList<>();
		try {
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が開始されました。");

			testApi.handleResponse(testJSONList, chunk -> {
				testReults.add(chunk);
			}, () -> {
				//event:message_endを認識できた場合、チャンク受信を完了。
				isComplete.set(true);
				System.out.println("受信したチャンクの中身；" + testReults);
			});

			//フラグチェック
			if(isComplete.get()== false) {
				throw new IllegalStateException("最終チャンクが存在しないため例外としました。");
			}
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が正常に終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			System.out.println(resultError);
			System.out.println("正しく例外が発生した");
		}
	}

	/**
	 *テストパターン　異常系　チャンクのJson構造が不正のためエラーとなる：括弧未完
	 */
	@Test
	public void test2_2_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2_2_1";

		//レスポンス処理の処理結果フラグ
		AtomicBoolean isComplete = new AtomicBoolean(false);

		//テスト用のレスポンスJSONリスト。
		List<String> testJSONList = List.of(
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"これは\",\"from_variable_selector\":null", // 括弧未完
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"テストの\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"レスポンス\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"です。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"次で\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"おわり。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message_end\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"終わりませんでした\",\"from_variable_selector\":null}");
		List<String> testReults = new ArrayList<>();
		try {
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が開始されました。");

			testApi.handleResponse(testJSONList, chunk -> {
				testReults.add(chunk);
			}, () -> {
				//event:message_endを認識できた場合、チャンク受信を完了。
				isComplete.set(true);
				System.out.println("受信したチャンクの中身；" + testReults);
			});
			//アサート
			List<String> assertResult = List.of("これは", "テストの", "レスポンス", "です。", "次で", "おわり。");

			assertEquals(assertResult, testReults);
			//フラグチェック
			assertTrue("最終チャンクの受信を認識できていません。", isComplete.get());

			System.out.println("**********************************************");
			System.out.println(resultOutput + "が正常に終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			System.out.println(resultError);
			System.out.println("正しく例外が発生した");
		}
	}
	
	/**
	 *テストパターン　異常系　チャンクのJson構造が不正のためエラーとなる：カンマ抜け
	 */
	@Test
	public void test2_2_2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2_2_2";

		//レスポンス処理の処理結果フラグ
		AtomicBoolean isComplete = new AtomicBoolean(false);

		//テスト用のレスポンスJSONリスト。
		List<String> testJSONList = List.of(
				"data: {\"event\":\"message\"\"conversation_id\":\"...\"\"message_id\":\"...\"\"created_at\":1234567890\"task_id\":\"...\",\"id\":\"...\"\"answer\":\"これは\"\"from_variable_selector\":null", // カンマ抜け
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"テストの\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"レスポンス\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"です。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"次で\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"おわり。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message_end\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"終わりませんでした\",\"from_variable_selector\":null}");
		List<String> testReults = new ArrayList<>();
		try {
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が開始されました。");

			testApi.handleResponse(testJSONList, chunk -> {
				testReults.add(chunk);
			}, () -> {
				//event:message_endを認識できた場合、チャンク受信を完了。
				isComplete.set(true);
				System.out.println("受信したチャンクの中身；" + testReults);
			});
			//アサート
			List<String> assertResult = List.of("これは", "テストの", "レスポンス", "です。", "次で", "おわり。");

			assertEquals(assertResult, testReults);
			//フラグチェック
			assertTrue("最終チャンクの受信を認識できていません。", isComplete.get());

			System.out.println("**********************************************");
			System.out.println(resultOutput + "が正常に終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			System.out.println(resultError);
			System.out.println("正しく例外が発生した");
		}
	}
	
	/**
	 *テストパターン　異常系　チャンクのJson構造が不正のためエラーとなる：配列形式
	 */
	@Test
	public void test2_2_3() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2_2_3";

		//レスポンス処理の処理結果フラグ
		AtomicBoolean isComplete = new AtomicBoolean(false);

		//テスト用のレスポンスJSONリスト。
		List<String> testJSONList = List.of(
				"data: [{\"event\":\"message\"\"conversation_id\":\"...\"\"message_id\":\"...\"\"created_at\":1234567890\"task_id\":\"...\",\"id\":\"...\"\"answer\":\"これは\"\"from_variable_selector\":null]", // 配列形式
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"テストの\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"レスポンス\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"です。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"次で\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"おわり。\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message_end\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"終わりませんでした\",\"from_variable_selector\":null}");
		List<String> testReults = new ArrayList<>();
		try {
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が開始されました。");

			testApi.handleResponse(testJSONList, chunk -> {
				testReults.add(chunk);
			}, () -> {
				//event:message_endを認識できた場合、チャンク受信を完了。
				isComplete.set(true);
				System.out.println("受信したチャンクの中身；" + testReults);
			});
			//アサート
			List<String> assertResult = List.of("これは", "テストの", "レスポンス", "です。", "次で", "おわり。");

			assertEquals(assertResult, testReults);
			//フラグチェック
			assertTrue("最終チャンクの受信を認識できていません。", isComplete.get());

			System.out.println("**********************************************");
			System.out.println(resultOutput + "が正常に終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			System.out.println(resultError);
			System.out.println("正しく例外が発生した");
		}
	}
	/**
	 *テストパターン　異常系　チャンク内の「answer」がnullのためエラーとなる。
	 */
	@Test
	public void test2_3() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2_3";

		//レスポンス処理の処理結果フラグ
		AtomicBoolean isComplete = new AtomicBoolean(false);

		//テスト用のレスポンスJSONリスト。
		List<String> testJSONList = List.of(
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":null,\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"null,\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"null,\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"null,\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"null,\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"null,\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"終わりませんでした\",\"from_variable_selector\":null}");
		List<String> testReults = new ArrayList<>();
		try {
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が開始されました。");

			testApi.handleResponse(testJSONList, chunk -> {
				testReults.add(chunk);
			}, () -> {
				//event:message_endを認識できた場合、チャンク受信を完了。
				isComplete.set(true);
				System.out.println("受信したチャンクの中身；" + testReults);
			});

			//アサート
			List<String> assertResult = List.of("これは", "テストの", "レスポンス", "です。", "次で", "おわり。");

			assertEquals(assertResult, testReults);
			//フラグチェック
			assertTrue("最終チャンクの受信を認識できていません。", isComplete.get());

			System.out.println("**********************************************");
			System.out.println(resultOutput + "が正常に終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			System.out.println(resultError);
			System.out.println("正しく例外が発生した");
		}
	}
	/**
	 *テストパターン　異常系　チャンク内の「answer」が空白のためエラーとなる：半角
	 */
	@Test
	public void test2_4_1() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2_4_1";

		//レスポンス処理の処理結果フラグ
		AtomicBoolean isComplete = new AtomicBoolean(false);

		//テスト用のレスポンスJSONリスト。
		List<String> testJSONList = List.of(
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\": \"\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"  \",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"   \",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"    \",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\" \",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\" \",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"終わりませんでした\",\"from_variable_selector\":null}");
		List<String> testReults = new ArrayList<>();
		try {
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が開始されました。");

			testApi.handleResponse(testJSONList, chunk -> {
				testReults.add(chunk);
			}, () -> {
				//event:message_endを認識できた場合、チャンク受信を完了。
				isComplete.set(true);
				System.out.println("受信したチャンクの中身；" + testReults);
			});

			//アサート
			List<String> assertResult = List.of("これは", "テストの", "レスポンス", "です。", "次で", "おわり。");

			assertEquals(assertResult, testReults);
			//フラグチェック
			assertTrue("最終チャンクの受信を認識できていません。", isComplete.get());

			System.out.println("**********************************************");
			System.out.println(resultOutput + "が正常に終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			System.out.println(resultError);
			System.out.println("正しく例外が発生した");
		}
	}
	/**
	 *テストパターン　異常系　チャンク内の「answer」が空白のためエラーとなる：全角
	 */
	@Test
	public void test2_4_2() {
		String className = new Object() {
		}.getClass().getName();
		String resultOutput = className + "のテストパターン2_4_2";

		//レスポンス処理の処理結果フラグ
		AtomicBoolean isComplete = new AtomicBoolean(false);

		//テスト用のレスポンスJSONリスト。
		List<String> testJSONList = List.of(
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":　\"\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"　　\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"　　　\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"　　　　\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"　\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"　\",\"from_variable_selector\":null}",
				"data: {\"event\":\"message\",\"conversation_id\":\"...\",\"message_id\":\"...\",\"created_at\":1234567890,\"task_id\":\"...\",\"id\":\"...\",\"answer\":\"終わりませんでした\",\"from_variable_selector\":null}");
		List<String> testReults = new ArrayList<>();
		try {
			System.out.println("**********************************************");
			System.out.println(resultOutput + "が開始されました。");

			testApi.handleResponse(testJSONList, chunk -> {
				testReults.add(chunk);
			}, () -> {
				//event:message_endを認識できた場合、チャンク受信を完了。
				isComplete.set(true);
				System.out.println("受信したチャンクの中身；" + testReults);
			});

			//アサート
			List<String> assertResult = List.of("これは", "テストの", "レスポンス", "です。", "次で", "おわり。");

			assertEquals(assertResult, testReults);
			//フラグチェック
			assertTrue("最終チャンクの受信を認識できていません。", isComplete.get());

			System.out.println("**********************************************");
			System.out.println(resultOutput + "が正常に終了しました。");

		} catch (Exception e) {
			String resultError = String.format("エラーが発生しました。内容は{%s}", e);
			System.out.println(resultError);
			System.out.println("正しく例外が発生した");
		}
	}
}
