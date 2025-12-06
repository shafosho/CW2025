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
 * Loads the FXML layout, and sets up the primary stage.
 */
public class Main extends Application {

    private static final int WINDOW_WIDTH = 650;
    private static final int WINDOW_HEIGHT = 650;
    private static final String APP_TITLE = "TetrisJFX";

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");

        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        // Get the controller to pass to the Game Logic
        GuiController c = fxmlLoader.getController();

        // Set up the window
        primaryStage.setTitle(APP_TITLE);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen(); // Appear in the middle of monitor
        primaryStage.show();

        // Initialize the Game Logic
        new GameController(c);
    }

    public static void main(String[] args) {
        launch(args);
    }
}