/**
 * 
 */
package app.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 共通関数クラス
 * @author 
 * @version 1.0
 */
public class CommonFunction {

	/************
	* メソッド名：Nullおよび空白チェック処理
	* 処理内容：可変長引数の中身をチェック。
	* @param checkStrings チェックしたいString型の変数（複数可能）
	************/
	public static void checkNullBlank(String... checkStrings) {
		int checkNum = checkStrings.length;
		StringBuilder errorMessage = new StringBuilder();

		for (int i = 0; i < checkNum; i++) {
			if (checkStrings[i] == null) {
				errorMessage.append("項目のうち").append(i).append("番目がnullです\n");
				continue;
			}
			if (checkStrings[i].isBlank()) {
				errorMessage.append("項目のうち").append(i).append("番目が空白です\n");
				continue;
			}
		}
		if (errorMessage.length() > 0) {
			throw new IllegalArgumentException("入力チェックエラー: " + errorMessage.toString());
		}
	}

	/**
	 * 設定ファイルを読み込む（プロパティ読み込みのみ）
	 * @param filePath 設定ファイルのパス
	 * @return 
	 * @throws IOException 読み込み失敗時
	 */
	public static Properties load(String proFile) throws IOException {
		Properties props = new Properties();
		//		try (InputStream proIn = CommonFunction.class.getClass().getClassLoader().getResourceAsStream(proFile)) {
		try (InputStream proIn = CommonFunction.class.getResourceAsStream(proFile)) {
			if (proIn == null) {
				throw new IllegalArgumentException(proFile + ":読み込みファイルが見つかりません。");
			}
			props.load(proIn);
			return props;
		}
	}

	/**
	 * アプリ起動PCのMacアドレス取得メソッド　動作未検証
	 * @return Macアドレス（例：00-1A-2B-3C-4D-5E/取得できない時はnull）
	 * @throws IOException 読み込み失敗時
	 */
	public static String getMacAddress() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				// ループバック、仮想NIC、現在使えないNICは除外
				if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
					continue;
				}

				byte[] mac = ni.getHardwareAddress();
				//MACアドレスが存在しないまたは、長さがMACアドレスの6バイト以外は排除。
				if (mac != null && mac.length == 6) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
					return sb.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // 必要に応じてログ出力
		}
		return null;
	}

	/**
	 * Java→JavaScriptへのエスケープ処理
	 * @param input　文字列型のチェック対象
	 * @return input エスケープ処理済み
	 */
	public static String escapeForJS(String input) {
	    if (input == null) return "\"\"";
	    String escaped = input
	        .replace("\\", "\\\\")
	        .replace("\"", "\\\"")
	        .replace("\n", "\\n")
	        .replace("\r", "")
	        .replace("'", "\\'")
	        .replace("\t", "\\t");
	    return "\"" + escaped + "\"";  // JS文字列として囲う
	}
}
