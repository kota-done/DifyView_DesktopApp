package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import app.ViewLauncher;


public class IFrameViewTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() {
		try {
			//IFrameViewテスト
//			IFrameView frame = new IFrameView();
//			frame.setView("テストウィンドウ", 400,300);
			
			//Viewlauncherテスト
<<<<<<< Updated upstream
			
=======
			ViewLauncher lnc = new ViewLauncher();
			lnc.launch();
			Thread.sleep(6000);
>>>>>>> Stashed changes
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("エラー発生；"+e.getMessage());
		}
		
		
	}

}
