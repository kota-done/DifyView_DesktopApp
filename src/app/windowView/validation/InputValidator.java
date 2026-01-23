package app.windowView.validation;

public class InputValidator {

	// 最大長 3000
	private static final int MAX_LEN = 3000;

	/**
	 * 入力チェック（Fail-fast）
	 * NGなら例外を投げる。
	 *
	 * バリデーション方針：
	 * - 画面のJavaScriptのtrim後空はNG
	 * - CRLF/CRはLFに正規化（Windows対策）
	 * - 許可する制御文字はLFとTABのみ
	 * - 入力の最大長：3000
	 *
	 * @param input ユーザー入力
	 * @return 正規化後の文字列（LFに統一）
	 */
	public String validateExecute(String input) {
		if (input == null) {
			throw new IllegalArgumentException("入力してください。");
		}

		// 改行統一（CRLF/CR → LF）
		String normalized = input.replace("\r\n", "\n").replace("\r", "\n");

		// 空チェック
		if (normalized.trim().isEmpty()) {
			throw new IllegalArgumentException("入力してください。");
		}

		// 最大長
		if (normalized.length() > MAX_LEN) {
			throw new IllegalArgumentException("入力が長すぎます。最大" + MAX_LEN + "文字です。");
		}
		// 制御文字チェック（許可：LF(\n) と TAB(\t) のみ）
		for (int i = 0; i < normalized.length(); i++) {
			char c = normalized.charAt(i);
			if (c < 0x20) { // C0制御文字領域
				if (c != '\n' && c != '\t') {
					throw new IllegalArgumentException("使用できない制御文字が含まれています。");
				}
			}
			if (c == 0x7F) { // DEL
				throw new IllegalArgumentException("使用できない制御文字が含まれています。");
			}
		}

		return normalized;
	}
}