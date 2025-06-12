package app.config;

/**
 * 設定ファイルのDtoクラス。
 * @author 
 * @version 1.0
 */
class AppSettingDto {
	
	/*
	 属性:タイトル、表示URL、ウィンドウ幅、ウィンドウ高さ
	*/
	private final String logPass;
	private final String logFileExtensionString;
	private final String title;
	private final String windUrl;
	private final int windWidth;
	private final int windHeight;
	public static AppSettingDto appSettingDto;
	

	//************
	// メソッド名：引数付きコンストラクタ
	// 処理内容：インスタンス生成用コンストラクタ
	/************/
	
	private AppSettingDto(String logPass,
			              String logFileExtension,
			              String title,
			              String windUrl,
			              int windWidth,
			              int windHeight){
		this.logPass= logPass;
		this.logFileExtensionString = logFileExtension;
		this.title = title;
		this.windUrl = windUrl;
		this.windWidth = windWidth;
		this.windHeight = windHeight;
	}
	
	//************
	// メソッド名：インスタンス生成メソッド
	// 処理内容：重複チェックをした後に、引数つきコンストラクタを呼び出す。
	/************/
	public static void createInstance(String logPass,
            String logFileExtension,
            String title,
            String windUrl,
            int windWidth,
            int windHeight) {
		if(appSettingDto == null) {
				appSettingDto = new AppSettingDto(logPass, logFileExtension, title, windUrl, windWidth, windHeight);
		}else {
			//インスタンが重複している場合、エラーをスローする。キャッチ先：Main or SettingLoader
			throw new IllegalStateException();
		}	
	}
	//************
	// メソッド名：インスタンス取得処理
	// 処理内容：インスタンスが生成済かチェックして、返す。
	/************/
	public static AppSettingDto getInstance() {
		if(appSettingDto == null) {
			throw new IllegalStateException();
		}
		return appSettingDto;
	}
	
	//************
	// メソッド名：フィールド変数のGetter
	// 処理内容：各フィールド変数の中身を取得。
	/************/
	public String getLogPass() {
		return logPass;
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
