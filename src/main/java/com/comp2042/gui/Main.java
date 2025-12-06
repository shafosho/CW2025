package com.comp2042.gui;

import com.comp2042.gameLogic.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * The main entry point for the Tetris application.
 * Initializes the application window and loads the Start Menu.
 */
public class Main extends Application {

    private static final int WINDOW_WIDTH = 650;
    private static final int WINDOW_HEIGHT = 650;
    private static final String APP_TITLE = "TetrisJFX";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // LOAD START MENU instead of gameLayout
        URL location = getClass().getClassLoader().getResource("startMenuPage.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle(APP_TITLE);
        // Use the defined constants
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();

        // MenuController will initialize GameController when the user clicks "Start".
    }

    public static void main(String[] args) {
        launch(args);
    }
}