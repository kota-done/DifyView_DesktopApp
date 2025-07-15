package app.windowView.window;

import window_interface.UiIniExecutor;

public class UiIniWrapper implements UiIniExecutor {

	@Override
	public void runLater(Runnable r) {
		javafx.application.Platform.runLater(r);
	}

}
