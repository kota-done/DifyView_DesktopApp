package app.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CheckWindow extends Application{
	@Override
    public void start(Stage stage) {
    	System.out.println("起動はしてるよ");
        stage.setScene(new Scene(new Label("Hello, JavaFX!"), 300, 200));
        stage.show();
    }

}
