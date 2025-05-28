package app;

public class ViewLauncher {
	public void launch() {
		
		//ConfigLoaderでDTOクラスでまとめてWindowの設定を読み出す
		
		//フレーム起動
		IFrameView frame = new IFrameView();		
		frame.setView("title", 500, 500);
		
	}
	

	
}
