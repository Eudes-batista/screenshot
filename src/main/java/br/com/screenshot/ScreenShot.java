package br.com.screenshot;

import javafx.application.Application;
import javafx.stage.Stage;

public class ScreenShot extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        final ScreenshotForm screenshotForm = new ScreenshotForm(stage);
        screenshotForm.createScene();
    }

    public static void main(String... a) {
        launch(a);
    }

}
