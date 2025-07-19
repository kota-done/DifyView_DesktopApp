package app.windowView.config;

/**
 * 設定ファイルのDtoクラス
 * @author 
 * @version 1.1
 * 修正：
 * 7/19　windURL→apiURLに修正 apiKey追加
 */
class AppSettingFactory {
	/*
	 属性:設定ファイルDto
	*/
	private AppSettingDto appSettingDto;

	/************
	* メソッド名：インスタンス取得処理
	* 処理内容：インスタンスが生成済かチェックして、返す。
	* @return AppSettingDto
	* @throws IllegalStateException すでに生成済みの場合
	/************/
	public AppSettingDto getInstance() {
		if (appSettingDto == null) {
			throw new IllegalStateException("生成処理未実施エラー：インスタンスが存在しません。");
		}
		return appSettingDto;
	}

	/************
	* メソッド名：インスタンス生成メソッド
	* 処理内容：引数つきコンストラクタを呼び出す。
	* AppSettingDtoインスタンスを生成
	* @param logPath
	* @param logFileExtension
	* @param title
	* @param windUrl
	* @param windWidth
	* @param windHeight
	* @return void
	************/
	void createInstance(String logPath,
			String logFileExtension,
			String title,
			String apiUrl,
			String apiKey,
			int windWidth,
			int windHeight) {

		//引数付きコンストラクタ呼び出し。
		appSettingDto = new AppSettingDto(logPath, logFileExtension, title, apiUrl,apiKey, windWidth, windHeight);
	}
}
