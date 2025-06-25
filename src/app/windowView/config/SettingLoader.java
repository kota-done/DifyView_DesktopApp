package app.windowView.config;

import java.util.Properties;

import app.util.CommonFunction;

public class SettingLoader {

	/**
	 * 読み込んだプロパティからDTOを生成し、Factoryに登録する
	 * @param props　呼び出し元で生成したPropertiesオブジェクト
	 * @return void
	 */
	public static AppSettingDto startSettings(Properties props) {
		int windWidth;
		int windHeight;
		// Factoryメソッド呼び出しのためのインスタンス準備
		AppSettingFactory fact = new AppSettingFactory();
		
		//　引数のプロパティオブジェクトのnullチェック
		if (props == null) {
			throw new IllegalStateException("設定ファイルが読み込まれていません。");
		}
		String logPath = props.getProperty("LOGPATH").trim();
		String logFileExtension = props.getProperty("LOGFILEEX").trim();
		String title = props.getProperty("WINDOW_TITLE").trim();
		String windUrl = props.getProperty("WINDOW_URL").trim();
		
		//int型の引数の整数チェック。整数以外はNG。設定値が取得できなかった場合も例外。
		try {
			String windWidthStr = props.getProperty("WINDOW_WIDTH").trim();
			String windHeightStr = props.getProperty("WINDOW_HEIGHT").trim();
			windWidth = Integer.parseInt(windWidthStr);
			windHeight = Integer.parseInt(windHeightStr);
		} catch (NullPointerException | NumberFormatException e) {
			throw new IllegalStateException("設定ファイルのint型の項目に問題があります。：" + e);
		}

		//引数のNULLおよび空白チェック。int型は呼び出し元のLoaderクラスのメソッドでint型と制限しているのでnull混入はコンパイルで弾ける。
		//例外キャッチは呼び出し元。
		CommonFunction.checkNullBlank(logPath, logFileExtension, title, windUrl);

		fact.createInstance(logPath, logFileExtension, title, windUrl, windWidth, windHeight);
		return fact.getInstance();
	}
}
