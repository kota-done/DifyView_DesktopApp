/**
 * 
 */
package app.util;

import java.io.IOException;
import java.io.InputStream;
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
			if(proIn == null) {
				throw new IllegalArgumentException(proFile+":読み込みファイルが見つかりません。");
			}
			props.load(proIn);
			return props;
		}
	}
}
