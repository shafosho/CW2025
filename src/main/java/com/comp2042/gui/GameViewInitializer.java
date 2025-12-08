package com.comp2042.gui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Handles the graphical setup and initialization of the main game board elements.
 * This class was extracted from GuiController.
 */
public class GameViewInitializer {

    private final int BRICK_SIZE;
    private final GuiController controller;

    public GameViewInitializer(GuiController controller, int brickSize) {
        this.controller = controller;
        this.BRICK_SIZE = brickSize;
    }

    // Helper method to retrieve the color map for the grid initialization
    private Paint getFillColor(int i) {
        return controller.getFillColor(i);
    }

    /**
     * Initializes the background board, falling brick panel, and shadow brick panel based on the board matrix size.
     * @param boardMatrix The game's complete matrix (e.g., 22x10).
     * @param gamePanel The GridPane for the locked/background blocks.
     * @param brickPanel The GridPane for the falling block.
     * @param shadowBrickPanel The GridPane for the ghost/shadow block.
     * @return An array of three initialized Rectangle arrays: [0]=Display Matrix, [1]=Falling Rectangles, [2]=Shadow Rectangles.
     */
    public Rectangle[][][] initializeGameGrids(int[][] boardMatrix, GridPane gamePanel, GridPane brickPanel, GridPane shadowBrickPanel) {
        Rectangle[][] displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        Rectangle[][] rectangles = new Rectangle[4][4]; // Max size of a brick
        Rectangle[][] shadowRectangles = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        // 1. Initialize Display Matrix (Locked Blocks)
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        // 2. Initialize Falling Brick Panel (4x4)
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT); // Initial state is transparent
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        // 3. Initialize Shadow Rectangles
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rect.setFill(Color.TRANSPARENT);
                shadowRectangles[i][j] = rect;
                shadowBrickPanel.add(rect, j, i - 2);
            }
        }

        // Return all initialized arrays
        return new Rectangle[][][]{displayMatrix, rectangles, shadowRectangles};
    }
}