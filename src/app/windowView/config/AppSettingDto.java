package app.windowView.config;

/**
 * 設定ファイルのDtoクラス
 * @author 
 * @version 1.1
 */
public class AppSettingDto {
	
	/*
	 属性:タイトル、表示URL、ウィンドウ幅、ウィンドウ高さ
	*/
	private final String logPath;
	private final String logFileExtensionString;
	private final String title;
	private final String windUrl;
	private final int windWidth;
	private final int windHeight;

	/************
	* メソッド名：引数付きコンストラクタ
	* 処理内容：インスタンス生成用コンストラクタ、同パッケージのAppSettingFactoryのcreateInstace()から呼ぶ。
	 * @param logPass
	 * @param logFileExtension
	 * @param title
	 * @param windUrl
	 * @param windWidth
	 * @param windHeight
	 */
	AppSettingDto(String logPath,
			              String logFileExtension,
			              String title,
			              String windUrl,
			              int windWidth,
			              int windHeight){
		this.logPath= logPath;
		this.logFileExtensionString = logFileExtension;
		this.title = title;
		this.windUrl = windUrl;
		this.windWidth = windWidth;
		this.windHeight = windHeight;
	}
	
	//************
	// メソッド名：フィールド変数のGetter
	// 処理内容：各フィールド変数の中身を取得。
	/************/
	public String getLogPath() {
		return logPath;
	}
	public String getLogFileExtensionString() {
		return logFileExtensionString;
	}
	public String getWindUrl() {
		return windUrl;
	}
	public int getWindWidth() {
		return windWidth;
	}
	public int getWindHeight() {
		return windHeight;
	}
	public String getTitle() {
		return title;
	}

}
