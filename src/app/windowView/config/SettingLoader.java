package app.windowView.config;

import java.util.Properties;

import app.util.CommonFunction;

public class SettingLoader {

	/**
	 * 読み込んだプロパティからDTOを生成し、Factoryに登録する
	 * @param props　呼び出し元で生成したPropertiesオブジェクト
	 * @return void
	 */
	public static void startSettings(Properties props,AppSettingFactory fact) {
		int windWidth;
		int windHeight;
		if (props == null) {
			throw new IllegalStateException("設定ファイルが読み込まれていません。");
		}
		String logPath = props.getProperty("LOGPATH");
		String logFileExtension = props.getProperty("LOGFILEEX");
		String title = props.getProperty("WINDOW_TITLE");
		String windUrl = props.getProperty("WINDOW_URL");
		try {
			String windWidthStr = props.getProperty("WINDOW_WIDTH");
			String windHeightStr = props.getProperty("WINDOW_HEIGHT");
			windWidth = Integer.parseInt(windWidthStr);
			windHeight = Integer.parseInt(windHeightStr);
		} catch (NullPointerException | NumberFormatException e) {
			throw new IllegalStateException("エラーの原因：" + e);
		}

		//引数のNULLおよび空白チェック。int型は呼び出し元のLoaderクラスのメソッドでint型と制限しているのでnull混入はコンパイルで弾ける。
		//例外キャッチは呼び出し元。
		CommonFunction.checkNullBlank(logPath, logFileExtension, title, windUrl);

		fact.createInstance(logPath, logFileExtension, title, windUrl, windWidth, windHeight);
	}
}
