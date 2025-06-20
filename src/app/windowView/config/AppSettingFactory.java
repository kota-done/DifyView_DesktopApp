package app.windowView.config;

/**
 * 設定ファイルDtoのFactoryクラス。
 * @author 
 * @version 1.0
 */
public class AppSettingFactory {
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
	AppSettingDto getInstance() {
		if (appSettingDto == null) {
			throw new IllegalStateException("生成処理未実施エラー：インスタンスが存在しません。");
		}
		return appSettingDto;
	}

	/************
	* メソッド名：インスタンス生成メソッド
	* 処理内容：引数つきコンストラクタを呼び出す。
	* AppSettingDtoインスタンスを生成
	* @param logPass
	* @param logFileExtension
	* @param title
	* @param windUrl
	* @param windWidth
	* @param windHeight
	* @return void
	************/
	void createInstance(String logPass,
			String logFileExtension,
			String title,
			String windUrl,
			int windWidth,
			int windHeight) {

		//引数付きコンストラクタ呼び出し。
		appSettingDto = new AppSettingDto(logPass, logFileExtension, title, windUrl, windWidth, windHeight);
	}
}
