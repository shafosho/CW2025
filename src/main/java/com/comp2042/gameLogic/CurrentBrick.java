package com.comp2042.gameLogic;

import com.comp2042.data.NextShapeInfo;
import com.comp2042.logic.bricks.Brick;

/**
 * Represents the active brick currently falling on the board.
 * It holds the brick type and tracks its rotation state.
 */
class CurrentBrick { // Package-private for encapsulation

    private Brick brick;
    private int currentShapeIndex = 0; // Renamed from 'currentShape' for clarity

    public CurrentBrick() {
    }

    /**
     * Calculates the next rotation state to check for collisions.
     * @return Info about the next possible shape
     */
    public NextShapeInfo getNextShape() {
        int nextIndex = currentShapeIndex;
        nextIndex = (++nextIndex) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextIndex), nextIndex);
    }

    /**
     * Gets the matrix of the brick in its current rotation.
     * @return The 2D array representing the shape
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShapeIndex);
    }

    /**
     * Updates the rotation index.
     * @param currentShapeIndex The new rotation state (0-3)
     */
    public void setCurrentShape(int currentShapeIndex) {
        this.currentShapeIndex = currentShapeIndex;
    }

    /**
     * Assigns a new brick to be the active falling piece.
     * @param brick The new brick type
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShapeIndex = 0;
    }

    /**
     * Gets the actual Brick object (e.g. TBrick, IBrick).
     * Used for the Hold functionality to swap bricks.
     * @return The underlying Brick object
     */
    public Brick getBrick() {
        return brick;
    }
}