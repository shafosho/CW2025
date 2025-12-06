package com.comp2042.gui;

import com.comp2042.data.DownData;
import com.comp2042.data.EventSource;
import com.comp2042.data.EventType;
import com.comp2042.data.MoveEvent;
import com.comp2042.data.ViewData;
import com.comp2042.gameLogic.InputEventListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.application.Platform;
import com.comp2042.data.ScoreEntry;
import java.util.List;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int BOARD_OFFSET_X = 32; // Matches FXML gameBoard layoutX
    private static final int BOARD_OFFSET_Y = 42; // Matches FXML gameBoard layoutY

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane shadowBrickPanel; // Shadow Grid

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label highScoreLabel;

    @FXML
    private Label scoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label linesLabel;

    @FXML
    private GridPane nextBrickPanel; // The Next Piece Grid

    @FXML
    private GridPane holdBrickPanel; // The Hold Piece Grid

    @FXML
    private VBox pauseMenu;

    @FXML
    private VBox instructionsPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] shadowRectangles; // The rectangles for the shadow
    private Rectangle[][] nextBrickRectangles; // The rectangles for the preview
    private Rectangle[][] holdBrickRectangles; // The rectangles for hold display

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.P) {
                    togglePause();
                    keyEvent.consume();
                    return;
                }

                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        // 1. Capture the full data (Score + Visuals)
                        DownData downData = eventListener.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));

                        // 2. Check for Score/Line Clears (Same logic as moveDown)
                        if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                            NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                            groupNotification.getChildren().add(notificationPanel);
                            notificationPanel.showScore(groupNotification.getChildren());
                        }

                        // 3. Refresh the board
                        refreshBrick(downData.getViewData());
                        keyEvent.consume();
                    }
                    // Feature: Hold Brick on 'C' Key
                    if (keyEvent.getCode() == KeyCode.C) {
                        refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });

        gameOverPanel.setVisible(false);
        // Connect the Game Over buttons
        gameOverPanel.setReplayHandler(event -> newGame(null));
        gameOverPanel.setExitHandler(event -> System.exit(0));

        pauseMenu.setVisible(false);

        // Ensure instructions are shown when the scene loads
        instructionsPanel.setVisible(true);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);

        // Feature: Initialize the Next Brick preview grid
        initNextBrickView();
        // Initialize the Hold Brick preview grid
        initHoldBrickView();

        // Feature: Load High Score
        List<ScoreEntry> topScores = new com.comp2042.gameLogic.HighScoreManager().getTopScores();
        if (!topScores.isEmpty()) {
            highScoreLabel.setText("TOP: " + topScores.get(0).getScore());
        } else {
            highScoreLabel.setText("TOP: 0");
        }
    }

    @FXML
    public void startGame(ActionEvent event) {
        instructionsPanel.setVisible(false);
        gamePanel.requestFocus(); // Give focus back to the game so keys work
        timeLine.play(); // Start the game
    }

    /**
     * Creates the 4x4 grid of empty rectangles for the Next Piece preview.
     */
    private void initNextBrickView() {
        nextBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);

                // Fix: Add rounded corners to match the main game style
                rectangle.setArcWidth(9);
                rectangle.setArcHeight(9);

                nextBrickRectangles[i][j] = rectangle;
                nextBrickPanel.add(rectangle, j, i);
            }
        }
    }

    // Initialize the Hold Grid (Same style as Next Brick)
    private void initHoldBrickView() {
        holdBrickRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setArcWidth(9);
                rectangle.setArcHeight(9);
                holdBrickRectangles[i][j] = rectangle;
                holdBrickPanel.add(rectangle, j, i);
            }
        }
    }

    private void togglePause() {
        if (isGameOver.get()) return;

        if (isPause.get()) {
            isPause.set(false);
            timeLine.play();
            pauseMenu.setVisible(false);
            gamePanel.setOpacity(1.0);
        } else {
            isPause.set(true);
            timeLine.stop();
            pauseMenu.setVisible(true);
            gamePanel.setOpacity(0.5);
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        // Initialize Shadow Rectangles
        shadowRectangles = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rect.setFill(Color.TRANSPARENT);
                shadowRectangles[i][j] = rect;
                shadowBrickPanel.add(rect, j, i - 2);
            }
        }

        // Apply Offset
        brickPanel.setLayoutX(gamePanel.getLayoutX() + BOARD_OFFSET_X + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + BOARD_OFFSET_Y + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        // Set shadow panel position to match game panel + Offset
        shadowBrickPanel.setLayoutX(gamePanel.getLayoutX() + BOARD_OFFSET_X);
        shadowBrickPanel.setLayoutY(gamePanel.getLayoutY() + BOARD_OFFSET_Y);

        // Feature: Show the next brick immediately when the game starts
        refreshNextBrick(brick);
        // Refresh hold brick
        refreshHoldBrick(brick);
        // Initial shadow draw
        refreshShadowBrick(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
    }

    private Paint getFillColor(int i) {
        switch (i) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.AQUA;
            case 2: return Color.BLUEVIOLET;
            case 3: return Color.DARKGREEN;
            case 4: return Color.YELLOW;
            case 5: return Color.RED;
            case 6: return Color.BEIGE;
            case 7: return Color.BURLYWOOD;
            default: return Color.WHITE;
        }
    }

    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Apply Offset
            brickPanel.setLayoutX(gamePanel.getLayoutX() + BOARD_OFFSET_X + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + BOARD_OFFSET_Y + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
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

    /**
     * Updates the visuals for the Next Brick preview panel with centering logic.
     */
    private void refreshNextBrick(ViewData brick) {
        int[][] nextData = brick.getNextBrickData();

        // 1. Clear previous state
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextBrickRectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }

        // 2. Find the actual bounds of the piece (min/max X and Y)
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

        if (!hasBlock) return; // Should not happen

        // 3. Calculate piece dimensions
        int pieceHeight = maxRow - minRow + 1;
        int pieceWidth = maxCol - minCol + 1;

        // 4. Calculate centering offset (Integers truncate, so this is approximate but better)
        int startRow = (4 - pieceHeight) / 2;
        int startCol = (4 - pieceWidth) / 2;

        // 5. Draw the piece with the offset applied
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (nextData[i][j] != 0) {
                    // Map local piece coordinates (i, j) to the centered grid coordinates
                    int targetRow = startRow + (i - minRow);
                    int targetCol = startCol + (j - minCol);

                    // Safety check to ensure we stay within the 4x4 grid
                    if (targetRow >= 0 && targetRow < 4 && targetCol >= 0 && targetCol < 4) {
                        nextBrickRectangles[targetRow][targetCol].setFill(getFillColor(nextData[i][j]));
                    }
                }
            }
        }
    }

    // Update the Hold Grid (Same centering logic as refreshNextBrick)
    private void refreshHoldBrick(ViewData brick) {
        int[][] holdData = brick.getHoldBrickData();

        // Clear previous state
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdBrickRectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }

        if (holdData == null) return; // Nothing held yet

        // Find bounds
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

        // Calculate offset
        int startRow = (4 - (maxRow - minRow + 1)) / 2;
        int startCol = (4 - (maxCol - minCol + 1)) / 2;

        // Draw
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (holdData[i][j] != 0) {
                    int targetRow = startRow + (i - minRow);
                    int targetCol = startCol + (j - minCol);
                    if (targetRow >= 0 && targetRow < 4 && targetCol >= 0 && targetCol < 4) {
                        holdBrickRectangles[targetRow][targetCol].setFill(getFillColor(holdData[i][j]));
                    }
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void moveDown(MoveEvent event) {
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

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
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
            // Get current high score from label text
            int currentTop = Integer.parseInt(highScoreLabel.getText().replace("TOP: ", ""));
            if (newVal.intValue() > currentTop) {
                highScoreLabel.setText("TOP: " + newVal);
                highScoreLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 24px;");
            }
        });
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);

        int finalScore = Integer.parseInt(scoreLabel.getText().replace("Score: ", ""));

        if (finalScore > 0) {
            // Wrap the dialog in Platform.runLater to avoid crashing the animation thread
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog("Player");
                dialog.setTitle("New High Score");
                dialog.setHeaderText("Congratulations!");
                dialog.setContentText("Enter your name:");

                Optional<String> result = dialog.showAndWait();
                String name = result.orElse("Player");

                new com.comp2042.gameLogic.HighScoreManager().addScore(name, finalScore);
                System.out.println("Score saved: " + finalScore + " for " + name);
            });
        }
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        pauseMenu.setVisible(false);
        gamePanel.setOpacity(1.0);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}