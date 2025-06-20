/**
 * 
 */
package app.util;

/**
 * 共通関数クラス
 * @author 
 * @version 1.0
 */
public class CommonFunction {
	
	/************
	* メソッド名：Nullおよび空白チェック処理
	* 処理内容：可変長引数の中身をチェック。
	************/
	public static void checkNullBlank(String ...checkStrings) {
		int checkNum = checkStrings.length;
		StringBuilder errorMessage = new StringBuilder();
		
		for(int i = 0;i<checkNum; i++) {
			if(checkStrings[i]==null) {
				errorMessage.append("項目のうち").append(i).append("番目がnullです\n");
				continue;
			}
			if(checkStrings[i].isBlank()) {
				errorMessage.append("項目のうち").append(i).append("番目が空白です\n");
				continue;
			}
		}
		if(errorMessage.length()>0) {
			throw new IllegalArgumentException("入力チェックエラー: " + errorMessage.toString());
		}
	}
}
