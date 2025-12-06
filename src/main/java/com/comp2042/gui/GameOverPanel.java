package com.comp2042.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * The panel displayed when the game ends.
 * Contains the "Game Over" message and navigation buttons.
 */
public class GameOverPanel extends BorderPane {

    private final Button replayButton;
    private final Button exitButton;

    public GameOverPanel() {
        // Matching the Pause Menu style (Transparent black, white border, rounded corners)
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        // Use a VBox to stack the text and buttons vertically
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20 0 20 0;");

        // Game Over Text
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");

        // Force the game over label to stretch to the full width of the container
        gameOverLabel.setMaxWidth(Double.MAX_VALUE);
        gameOverLabel.setStyle("-fx-alignment: center; -fx-text-fill: white; -fx-font-size: 40px;");

        // Play Again Button
        replayButton = new Button("Play Again");
        replayButton.getStyleClass().add("scoreClass");
        replayButton.setStyle("-fx-font-size: 20px; -fx-base: green; -fx-text-fill: white; -fx-cursor: hand; -fx-border-color: white; -fx-border-width: 1px;");

        // Quit Button
        exitButton = new Button("Quit");
        exitButton.getStyleClass().add("scoreClass");
        exitButton.setStyle("-fx-font-size: 20px; -fx-base: red; -fx-text-fill: white; -fx-cursor: hand; -fx-border-color: white; -fx-border-width: 1px;");

        // Add everything to the layout
        layout.getChildren().addAll(gameOverLabel, replayButton, exitButton);
        setCenter(layout);
    }

    /**
     * Sets the action to run when "Play Again" is clicked.
     * @param event The event handler (usually triggering a new game)
     */
    public void setReplayHandler(EventHandler<ActionEvent> event) {
        replayButton.setOnAction(event);
    }

    /**
     * Sets the action to run when "Quit" is clicked.
     * @param event The event handler
     */
    public void setExitHandler(EventHandler<ActionEvent> event) {
        exitButton.setOnAction(event);
    }
}