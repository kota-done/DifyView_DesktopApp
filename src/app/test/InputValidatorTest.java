package app.test;

import static org.junit.Assert.*;

import org.junit.Test;

import app.windowView.validation.InputValidator;

public class InputValidatorTest {

	private final InputValidator validator = new InputValidator();

	// =========================
	// 正常系
	// =========================
	
	/**
	 * No1: 正常の文字列（全角） → OK
	 */
	@Test
	public void validate_No1_normalZenkaku_OK() {
		String input = "バリデーションテストNo1（全角）";
		String result = validator.validateExecute(input);
		assertEquals(input, result);
	}

	/**
	 * No2: CRLF / CR → LF 正規化 → OK
	 */
	@Test
	public void validate_No2_normalizeLineEnding_OK() {
		String input = "バリデーションテストNo2\r\n"
				+ "バリデーションテストNo2\r"
				+ "バリデーションテストNo2";

		String result = validator.validateExecute(input);

		assertEquals(
				"バリデーションテストNo2\n"
						+ "バリデーションテストNo2\n"
						+ "バリデーションテストNo2",
				result);
	}

	/**
	 * No3: 3000文字までは許容 → OK
	 */
	@Test
	public void validate_No3_maxLengthJust_OK() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 3000; i++) {
			sb.append('a');
		}

		String input = sb.toString();
		String result = validator.validateExecute(input);
		assertEquals(input, result);
	}

	/**
	 * No4: LFおよびTABは許容 → OK
	 */
	@Test
	public void validate_No4_tabAndLf_OK() {
		String input = "バリデーションテストNo4\t"
				+ "バリデーションテストNo4\n"
				+ "バリデーションテストNo4";

		String result = validator.validateExecute(input);
		assertEquals(input, result);
	}

	// =========================
	// 異常系
	// =========================

	/**
	 * No5: null → NG
	 */
	@Test
	public void validate_No5_nullInput_NG() {
		try {
			validator.validateExecute(null);
			fail("例外が投げられるべき");
		} catch (IllegalArgumentException e) {
			assertEquals("入力してください。", e.getMessage());
		}
	}

	/**
	 * No6: trim後空 → NG
	 */
	@Test
	public void validate_No6_trimEmpty_NG() {
		try {
			validator.validateExecute("   \n\t   ");
			fail("例外が投げられるべき");
		} catch (IllegalArgumentException e) {
			assertEquals("入力してください。", e.getMessage());
		}
	}

	/**
	 * No7: 3001文字以上はエラー → NG
	 */
	@Test
	public void validate_No7_overMaxLength_NG() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 3001; i++) { // 3000超過
			sb.append('a');
		}

		try {
			validator.validateExecute(sb.toString());
			fail("例外が投げられるべき");
		} catch (IllegalArgumentException e) {
			assertEquals("入力が長すぎます。最大3000文字です。", e.getMessage());
		}
	}

	/**
	 * No8: 制御文字（C0）を含む → NG
	 */
	@Test
	public void validate_No8_rejects_C0_ESC_NG() {
		String input = "バリデーションテスト" + ((char) 0x1B) + "No8";

		try {
			validator.validateExecute(input);
			fail("ESC(0x1B) を含む入力は例外になるべきです。");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("使用できない制御文字"));
		}
	}

	/**
	 * No9: 制御文字（DEL 0x7F）を含む → NG
	 */
	@Test
	public void validate_No9_rejects_DEL_NG() {
		String input = "バリデーションテスト" + ((char) 0x7F) + "No9";

		try {
			validator.validateExecute(input);
			fail("DEL(0x7F) を含む入力は例外になるべきです。");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("使用できない制御文字"));
		}
	}

	/**
	 * No10: 制御文字（NUL 0x00）を含む → NG
	 */
	@Test
	public void validate_No10_rejects_NUL_NG() {
		String input = "バリデーションテスト" + ((char) 0x00) + "No10";

		try {
			validator.validateExecute(input);
			fail("NUL(0x00) を含む入力は例外になるべきです。");
		} catch (IllegalArgumentException e) {
			// 実装メッセージ固定なら固定一致でもOK。方針に合わせてNotNullのみ。
			assertTrue(e.getMessage().contains("使用できない制御文字"));
		}
	}
}