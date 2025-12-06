package com.comp2042.gui;

import com.comp2042.gameLogic.GameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controls the Start Menu logic.
 * Handles navigation between the Menu, Game, and other screens.
 */
public class MenuController {

    /**
     * Starts the game by loading the main game layout.
     * @param event The button click event (used to find the current stage)
     */
    @FXML
    public void onStartGame(ActionEvent event) throws IOException {
        // 1. Load the Game FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
        Parent root = fxmlLoader.load();

        // 2. Initialize Logic
        GuiController c = fxmlLoader.getController();
        new GameController(c);

        // 3. Get current stage and Preserve Window State
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        boolean isMaximized = stage.isMaximized();

        // 4. Switch Scene
        Scene scene = new Scene(root);

        stage.setTitle("Tetris - Playing");
        stage.setScene(scene);

        // 5. Restore Window State (Prevent shrinking)
        if (isMaximized) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        }

        // Only center if NOT maximized and just starting standard size
        if (!isMaximized && currentWidth == 600 && currentHeight == 600) {
            stage.centerOnScreen();
        }

        stage.show();
    }

    /**
     * Switches to the High Scores screen.
     */
    @FXML
    public void onHighScores(ActionEvent event) throws IOException {
        // 1. Load the High Scores FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("highScores.fxml"));
        Parent root = fxmlLoader.load();

        // 2. Get current stage and dimensions
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        boolean isMaximized = stage.isMaximized();

        // 3. Switch Scene
        Scene scene = new Scene(root);
        stage.setTitle("Tetris - High Scores");
        stage.setScene(scene);

        // 4. Restore Window State
        if (isMaximized) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        }
        stage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    public void onExit(ActionEvent event) {
        System.exit(0);
    }
}