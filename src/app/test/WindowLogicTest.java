package app.test;

import app.windowView.window.WindowLogic;

public class WindowLogicTest {
	
	public static void main(String arugs[]) {
		WindowLogic logic = new WindowLogic();
		try {
			logic.execute();
		} catch (Exception e) {
			throw  new IllegalStateException("何かしらの例外発生："+e);
		}
		
	}
}
