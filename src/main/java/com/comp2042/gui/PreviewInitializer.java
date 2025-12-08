package com.comp2042.gui;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Handles the graphical setup and storage of the Rectangle objects
 * for the Next Piece Queue and Hold Piece display areas.
 * This class was extracted from GuiController.
 */
public class PreviewInitializer {

    // Fields to hold the generated rectangles, previously in GuiController
    public Rectangle[][][] nextBrickRectangles; // 3D array: [WhichBrick][Row][Col]
    public Rectangle[][] holdBrickRectangles;

    private static final int BRICK_SIZE = 20;

    /**
     * Initializes the view elements and fills the rectangle arrays.
     * * @param nextBrickGrids Array of the 3 GridPane containers for the next queue.
     * @param holdBrickPanel The single GridPane container for the hold piece.
     */
    public PreviewInitializer(GridPane[] nextBrickGrids, GridPane holdBrickPanel) {
        initNextBrickView(nextBrickGrids);
        initHoldBrickView(holdBrickPanel);
    }

    /**
     * Creates the 4x4 grid of empty rectangles for the Next Piece preview.
     */
    private void initNextBrickView(GridPane[] nextBrickGrids) {
        nextBrickRectangles = new Rectangle[3][4][4]; // 3 bricks, 4x4 each

        for (int k = 0; k < 3; k++) { // Loop through the 3 grids
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcWidth(9);
                    rectangle.setArcHeight(9);

                    nextBrickRectangles[k][i][j] = rectangle;
                    nextBrickGrids[k].add(rectangle, j, i);
                }
            }
        }
    }

    /**
     * Initializes the 4x4 grid of empty rectangles for the Hold Piece preview.
     */
    private void initHoldBrickView(GridPane holdBrickPanel) {
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
}