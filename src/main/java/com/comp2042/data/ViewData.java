package com.comp2042.data;

import com.comp2042.gameLogic.MatrixOperations;
import java.util.ArrayList; // Added missing import
import java.util.List;      // Added missing import

/**
 * An immutable data object containing the state of the board and the moving piece.
 * Sent from the Logic layer to the GUI layer for rendering.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> nextBrickData;
    private final int[][] holdBrickData;
    private final int shadowY; // Y position of the shadow

    /**
     * Creates a snapshot of the current game view.
     * @param brickData The shape of the falling brick
     * @param xPosition The X coordinate of the brick
     * @param yPosition The Y coordinate of the brick
     * @param nextBrickData The shape of the next upcoming brick
     * @param holdBrickData The shape of the held block
     */
    // FIXED: Updated parameter type to List<int[][]>
    public ViewData(int[][] brickData, int xPosition, int yPosition, List<int[][]> nextBrickData, int[][] holdBrickData, int shadowY) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.holdBrickData = holdBrickData;
        this.shadowY = shadowY;
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

    // Return the list
    public List<int[][]> getNextBrickData() {
        List<int[][]> copy = new ArrayList<>();
        for (int[][] matrix : nextBrickData) {
            copy.add(MatrixOperations.copy(matrix));
        }
        return copy;
    }

    public int[][] getHoldBrickData() {
        return (holdBrickData == null) ? null : MatrixOperations.copy(holdBrickData);
    }

    public int getShadowY() {
        return shadowY;
    }
}