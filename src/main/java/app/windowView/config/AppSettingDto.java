package app.windowView.config;

/**
 * 設定ファイルのDtoクラス
 * @author 
 * @version 1.1
 * 修正：
 * 7/19　windURL→apiURLに修正
 */
public class AppSettingDto {
	
	/*
	 属性:タイトル、表示URL、ウィンドウ幅、ウィンドウ高さ
	*/
	private final String logPath;
	private final String logFileExtensionString;
	private final String title;
	private final String apiUrl;
	private final String apiKey;
	private final int windWidth;
	private final int windHeight;

	/************
	* メソッド名：引数付きコンストラクタ
	* 処理内容：インスタンス生成用コンストラクタ、同パッケージのAppSettingFactoryのcreateInstace()から呼ぶ。
	 * @param logPass
	 * @param logFileExtension
	 * @param title
	 * @param apidUrl
	 * @param windWidth
	 * @param windHeight
	 */
	AppSettingDto(String logPath,
			              String logFileExtension,
			              String title,
			              String apiUrl,
			              String apiKey,
			              int windWidth,
			              int windHeight){
		this.logPath= logPath;
		this.logFileExtensionString = logFileExtension;
		this.title = title;
		this.apiUrl = apiUrl;
		this.apiKey = apiKey;
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
	public String getApiUrl() {
		return apiUrl;
	}
	public String getApiKey() {
		return apiKey;
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
