package app.windowView.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import app.util.CommonFunction;

public class SettingLoader {
	
	private Properties props;

    /**
     * 設定ファイルを読み込む（プロパティ読み込みのみ）
     * @param filePath 設定ファイルのパス
     * @throws IOException 読み込み失敗時
     */
	public void load(String path) throws IOException {
		props = new Properties();
		try (InputStream proIn = getClass().getClassLoader().getResourceAsStream(path)) {
			if(proIn == null) {
				throw new IllegalArgumentException(path+":読み込みファイルが見つかりません。");
			}
			props.load(proIn);
		}
	}

    /**
     * 読み込んだプロパティからDTOを生成し、Factoryに登録する
     */
	public void startSettings() {
		int  windWidth;
		int windHeight;
		if(props == null) {
			throw new  IllegalStateException("設置ファイルが読み込まれていません。");
		}
		String logPass = props.getProperty("LOGPASS");
		String logFileExtension = props.getProperty("LOGFILEEX");
		String title = props.getProperty("WINDOW_TITLE");
		String windUrl = props.getProperty("WINDOW_URL");
		try {
			String windWidthStr = props.getProperty("WINDOW_WIDTH");
			String windHeightStr = props.getProperty("WINDOW_HEIGHT"); 
			windWidth = Integer.parseInt(windWidthStr);
			windHeight = Integer.parseInt(windHeightStr);
		} catch (NullPointerException | NumberFormatException e) {
			throw new IllegalStateException("エラーの原因："+e) ;
		}
		
		//引数のNULLおよび空白チェック。int型は呼び出し元のLoaderクラスのメソッドでint型と制限しているのでnull混入はコンパイルで弾ける。
		//例外キャッチは呼び出し元。
		CommonFunction.checkNullBlank(logPass, logFileExtension,title, windUrl);
		
		AppSettingFactory factory = new AppSettingFactory();
		factory.createInstance(logPass, logFileExtension, title, windUrl, windWidth, windHeight);
	}
}
