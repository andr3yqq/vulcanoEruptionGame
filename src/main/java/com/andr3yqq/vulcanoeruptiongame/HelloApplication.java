package com.andr3yqq.vulcanoeruptiongame;

import com.andr3yqq.vulcanoeruptiongame.ui.GameScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private static final double CANVAS_WIDTH = 720;
    private static final double CANVAS_HEIGHT = 520;
    private static final double SIDEBAR_WIDTH = 280;

    @Override
    public void start(Stage stage) {
        GameScreen screen = new GameScreen(CANVAS_WIDTH, CANVAS_HEIGHT, SIDEBAR_WIDTH);
        Scene scene = new Scene(screen.getRoot(), CANVAS_WIDTH + SIDEBAR_WIDTH, CANVAS_HEIGHT);
        var stylesheet = HelloApplication.class.getResource("/styles.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        stage.setTitle("Ugnikalnio evakuacija");
        stage.setScene(scene);
        stage.show();
    }

    static void main(String[] args) {
        launch();
    }
}
