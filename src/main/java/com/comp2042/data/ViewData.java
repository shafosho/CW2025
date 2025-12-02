package com.comp2042.data;

import com.comp2042.gameLogic.MatrixOperations;

/**
 * An immutable data object containing the state of the board and the moving piece.
 * Sent from the Logic layer to the GUI layer for rendering.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;

    /**
     * Creates a snapshot of the current game view.
     * @param brickData The shape of the falling brick
     * @param xPosition The X coordinate of the brick
     * @param yPosition The Y coordinate of the brick
     * @param nextBrickData The shape of the next upcoming brick
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}