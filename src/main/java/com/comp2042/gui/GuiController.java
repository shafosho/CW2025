package com.comp2042.gui;

import com.comp2042.data.*;
import com.comp2042.gameLogic.InputEventListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import com.comp2042.gameLogic.HighScoreManager;
import javafx.application.Platform;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int BOARD_OFFSET_X = 256;
    private static final int BOARD_OFFSET_Y = 40;

    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private GridPane shadowBrickPanel;
    @FXML private GridPane holdBrickPanel;
    @FXML private GameOverPanel gameOverPanel;
    @FXML private Label highScoreLabel;
    @FXML private Label highScoreNameLabel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label linesLabel;
    @FXML private GridPane nextBrick1;
    @FXML private GridPane nextBrick2;
    @FXML private GridPane nextBrick3;
    @FXML private VBox pauseMenu;
    @FXML private VBox instructionsPanel;
    @FXML private Button muteButton;

    private InputHandler inputHandler;
    private PreviewInitializer previewInitializer;
    private GameViewInitializer gameViewInitializer;
    private StyleManager styleManager;

    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private Rectangle[][] rectangles;
    private Rectangle[][] shadowRectangles;
    private Timeline timeLine;
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    private SoundManager soundManager;
    private int currentHighScore = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);

        styleManager = new StyleManager();
        gameViewInitializer = new GameViewInitializer(styleManager, BRICK_SIZE);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        inputHandler = new InputHandler(this, isPause, isGameOver);

        GridPane[] nextGrids = new GridPane[]{nextBrick1, nextBrick2, nextBrick3};
        previewInitializer = new PreviewInitializer(nextGrids, holdBrickPanel);

        gamePanel.setOnKeyPressed(inputHandler::handleKeyInput);

        gameOverPanel.setVisible(false);
        gameOverPanel.setReplayHandler(event -> newGame(null));
        gameOverPanel.setExitHandler(event -> System.exit(0));

        pauseMenu.setVisible(false);
        instructionsPanel.setVisible(true);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);

        List<ScoreEntry> topScores = new com.comp2042.gameLogic.HighScoreManager().getTopScores();
        if (!topScores.isEmpty()) {
            ScoreEntry topEntry = topScores.get(0);
            currentHighScore = topEntry.getScore();
            highScoreLabel.setText("TOP: " + currentHighScore);
            highScoreNameLabel.setText("(" + topEntry.getName() + ")");
        } else {
            currentHighScore = 0;
            highScoreLabel.setText("TOP: 0");
            highScoreNameLabel.setText("");
        }
        soundManager = new SoundManager(muteButton);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Refactor: Use GameViewInitializer to create and initialize the grid views
        Rectangle[][][] initializedRects = gameViewInitializer.initializeGameGrids(
                boardMatrix, gamePanel, brickPanel, shadowBrickPanel
        );

        // Unpack the initialized arrays
        this.displayMatrix = initializedRects[0];
        this.rectangles = initializedRects[1];
        this.shadowRectangles = initializedRects[2];

        // Layout consistency (Retaining the complex layout calls as requested)
        brickPanel.setLayoutX(gamePanel.getLayoutX() + BOARD_OFFSET_X + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + BOARD_OFFSET_Y + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
        shadowBrickPanel.setLayoutX(gamePanel.getLayoutX() + BOARD_OFFSET_X);
        shadowBrickPanel.setLayoutY(gamePanel.getLayoutY() + BOARD_OFFSET_Y);

        refreshNextBrick(brick);
        refreshHoldBrick(brick);
        refreshShadowBrick(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Updates the visuals for the Next Brick preview panel with centering logic.
     * Updated to handle a Queue of 3 Bricks.
     */
    private void refreshNextBrick(ViewData brick) {
        List<int[][]> nextDataList = brick.getNextBrickData();
        Rectangle[][][] nextRects = previewInitializer.nextBrickRectangles;

        // Loop using the size of the array from PreviewInitializer
        for (int k = 0; k < nextRects.length; k++) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    nextRects[k][i][j].setFill(Color.TRANSPARENT);
                }
            }

            if (k >= nextDataList.size()) continue;
            int[][] nextData = nextDataList.get(k);

            int minRow = 4, maxRow = 0, minCol = 4, maxCol = 0;
            boolean hasBlock = false;
            for (int i = 0; i < nextData.length; i++) {
                for (int j = 0; j < nextData[i].length; j++) {
                    if (nextData[i][j] != 0) {
                        if (i < minRow) minRow = i;
                        if (i > maxRow) maxRow = i;
                        if (j < minCol) minCol = j;
                        if (j > maxCol) maxCol = j;
                        hasBlock = true;
                    }
                }
            }
            if (!hasBlock) continue;

            int pieceHeight = maxRow - minRow + 1;
            int pieceWidth = maxCol - minCol + 1;
            int startRow = (4 - pieceHeight) / 2;
            int startCol = (4 - pieceWidth) / 2;

            for (int i = minRow; i <= maxRow; i++) {
                for (int j = minCol; j <= maxCol; j++) {
                    if (nextData[i][j] != 0) {
                        int targetRow = startRow + (i - minRow);
                        int targetCol = startCol + (j - minCol);
                        nextRects[k][targetRow][targetCol].setFill(styleManager.getFillColor(nextData[i][j]));
                    }
                }
            }
        }
    }

    // Update the Hold Grid (Same centering logic as refreshNextBrick)
    private void refreshHoldBrick(ViewData brick) {
        int[][] holdData = brick.getHoldBrickData();
        Rectangle[][] holdRects = previewInitializer.holdBrickRectangles; // Now using the correct reference

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdRects[i][j].setFill(Color.TRANSPARENT); // Using the correct array reference
            }
        }

        if (holdData == null) return;

        int minRow = 4, maxRow = 0, minCol = 4, maxCol = 0;
        boolean hasBlock = false;
        for (int i = 0; i < holdData.length; i++) {
            for (int j = 0; j < holdData[i].length; j++) {
                if (holdData[i][j] != 0) {
                    if (i < minRow) minRow = i;
                    if (i > maxRow) maxRow = i;
                    if (j < minCol) minCol = j;
                    if (j > maxCol) maxCol = j;
                    hasBlock = true;
                }
            }
        }
        if (!hasBlock) return;

        int startRow = (4 - (maxRow - minRow + 1)) / 2;
        int startCol = (4 - (maxCol - minCol + 1)) / 2;

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (holdData[i][j] != 0) {
                    int targetRow = startRow + (i - minRow);
                    int targetCol = startCol + (j - minCol);
                    if (targetRow >= 0 && targetRow < 4 && targetCol >= 0 && targetCol < 4) {
                        holdRects[targetRow][targetCol].setFill(styleManager.getFillColor(holdData[i][j]));
                    }
                }
            }
        }
    }

    // Draw the Shadow Brick
    private void refreshShadowBrick(ViewData brick) {
        // Clear previous shadow
        for (int i = 2; i < shadowRectangles.length; i++) {
            for (int j = 0; j < shadowRectangles[i].length; j++) {
                shadowRectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }
        // Apply Offset
        shadowBrickPanel.setLayoutX(gamePanel.getLayoutX() + BOARD_OFFSET_X);
        shadowBrickPanel.setLayoutY(gamePanel.getLayoutY() + BOARD_OFFSET_Y);

        int[][] shape = brick.getBrickData();
        int shadowY = brick.getShadowY();
        int startX = brick.getxPosition();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    // Coordinate mapping (i=row, j=col based on your matrix logic)
                    int targetY = shadowY + i;
                    int targetX = startX + j;

                    // Check bounds (safety)
                    if (targetY >= 2 && targetY < shadowRectangles.length &&
                            targetX >= 0 && targetX < shadowRectangles[0].length) {

                        shadowRectangles[targetY][targetX].setFill(Color.GRAY);
                        shadowRectangles[targetY][targetX].setOpacity(0.3);
                        shadowRectangles[targetY][targetX].setArcWidth(9);
                        shadowRectangles[targetY][targetX].setArcHeight(9);
                    }
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                styleManager.setRectangleData(board[i][j], displayMatrix[i][j]); // Use StyleManager
            }
        }
    }

    public void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Sets the InputEventListener and passes the listener down to the InputHandler class to enable game logic calls.
     * * @param eventListener The game logic listener.
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;

        // Fix: The InputHandler must receive the listener instance
        if (this.inputHandler != null) {
            this.inputHandler.setEventListener(eventListener);
        }
    }

    public void bindScore(IntegerProperty score, IntegerProperty level, IntegerProperty lines) {
        scoreLabel.textProperty().bind(score.asString("Score: %d"));
        levelLabel.textProperty().bind(level.asString("Level: %d"));
        linesLabel.textProperty().bind(lines.asString("Lines Cleared: %d"));

        // Speed Logic: For level changes
        level.addListener((observable, oldValue, newValue) -> {
            int newLevel = newValue.intValue();
            int newSpeed = Math.max(50, 400 - ((newLevel - 1) * 50));

            System.out.println("Level " + newLevel + ": Speed set to " + newSpeed + "ms");

            timeLine.stop();
            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(newSpeed),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
        });

        score.addListener((obs, oldVal, newVal) -> {
            // Fix: Compare against numeric high score (safer than text parsing)
            if (newVal.intValue() > currentHighScore) {
                highScoreLabel.setText("TOP: " + newVal);
                highScoreNameLabel.setText("(YOU!)"); // Update the name label
                styleManager.applyHighScoreStyle(highScoreLabel, highScoreNameLabel); // Use StyleManager
            }
        });
    }

    // Feature: Only show congratulations dialog if the score is actually a high score (Top 3)
    public void gameOver() {
        timeLine.stop();
        soundManager.stopMusic(); // Refactor: Delegate to SoundManager
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);

        int finalScore = Integer.parseInt(scoreLabel.getText().replace("Score: ", ""));

        // Use the new helper method in HighScoreManager to check if the score qualifies
        HighScoreManager manager = new HighScoreManager();

        if (manager.canEnterHighScore(finalScore)) {
            // Wrap the dialog in Platform.runLater to avoid crashing the animation thread
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog("Player");
                dialog.setTitle("New High Score");
                dialog.setHeaderText("Congratulations!");
                dialog.setContentText("Enter your name:");

                Optional<String> result = dialog.showAndWait();
                String name = result.orElse("Player");

                manager.addScore(name, finalScore);
                System.out.println("Score saved: " + finalScore + " for " + name);
            });
        }
    }

    // Fix: Reordered lines to unpause flags FIRST so visual update isn't blocked
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        soundManager.stopAndRestartMusic(); // Refactor: Delegate to SoundManager

        // Unpause FIRST
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();

        pauseMenu.setVisible(false);
        gamePanel.setOpacity(1.0);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    // Fix: Changed from private to public so GameController can force a refresh
    public void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Apply Offset
            brickPanel.setLayoutX(gamePanel.getLayoutX() + BOARD_OFFSET_X + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + BOARD_OFFSET_Y + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    styleManager.setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]); // Use StyleManager
                }
            }
            // Feature: Update the preview whenever the active brick updates (e.g. spawns)
            refreshNextBrick(brick);
            // Update hold display
            refreshHoldBrick(brick);
            // Update shadow
            refreshShadowBrick(brick);
        }
    }

    /**
     * Feature: Sound System - Mute Toggle
     */
    @FXML
    public void toggleMute(ActionEvent event) {
        soundManager.toggleMute(event);
        // Important: Return focus to the game so keyboard controls keep working!
        gamePanel.requestFocus();
    }

    // Change 'private' to 'public' so InputHandler can call it
    public void togglePause() {
        if (isGameOver.get()) return;

        if (isPause.get()) {
            isPause.set(false);
            timeLine.play();
            pauseMenu.setVisible(false);
            gamePanel.setOpacity(1.0);
            soundManager.playMusic(); // Delegating audio to SoundManager
        } else {
            isPause.set(true);
            timeLine.stop();
            pauseMenu.setVisible(true);
            gamePanel.setOpacity(0.5);
            soundManager.pauseMusic(); // Delegating audio to SoundManager
        }
    }

    /**
     * Helper method to display score pop-ups on the game screen.
     * This method is called by the InputHandler.
     *
     * @param scoreBonus The score value to display in the notification panel.
     */
    public void showScoreNotification(int scoreBonus) {
        NotificationPanel notificationPanel = new NotificationPanel("+" + scoreBonus);
        groupNotification.getChildren().add(notificationPanel);
        notificationPanel.showScore(groupNotification.getChildren());
    }
}