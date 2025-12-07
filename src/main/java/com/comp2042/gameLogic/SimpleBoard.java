package com.comp2042.gameLogic;

import com.comp2042.data.ClearRow;
import com.comp2042.data.NextShapeInfo;
import com.comp2042.data.ViewData;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Handles the game logic, the grid, and the falling bricks.
 */
public class SimpleBoard implements Board {

    // Refactor: Replaced magic number '4' and '0' with named constants for spawning
    private static final int SPAWN_COL = 4;
    private static final int SPAWN_ROW = 0;

    private final int rows;
    private final int cols;
    private final BrickGenerator brickGenerator;
    private final CurrentBrick currentBrick;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    // Feature: Hold Variables
    private Brick holdBrick;
    private boolean canHold = true; // Can only hold once per turn

    // Feature: Preview Queue (3 Bricks)
    private final Deque<Brick> nextBricks;

    /**
     * Creates the board with a specific height and width.
     * @param rows The height of the board (number of rows)
     * @param cols The width of the board (number of columns)
     */
    public SimpleBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        currentGameMatrix = new int[rows][cols];
        brickGenerator = new RandomBrickGenerator();
        currentBrick = new CurrentBrick();
        score = new Score();

        // Initialize the queue with 3 random bricks
        nextBricks = new ArrayDeque<>();
        for (int i = 0; i < 3; i++) {
            nextBricks.add(brickGenerator.getBrick());
        }
    }

    /**
     * Tries to move the brick down one step.
     * @return true if it moved down, false if it hit something
     */
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, currentBrick.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Moves the falling brick to the left.
     * @return true if moved successfully
     */
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, currentBrick.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Moves the falling brick to the right.
     * @return true if moved successfully
     */
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, currentBrick.getCurrentShape(), (int) p.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Rotates the brick 90 degrees if it fits.
     * @return true if rotated, false if it couldn't turn
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = currentBrick.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            currentBrick.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    /**
     * Spawns a new random brick at the top of the board.
     * @return false if the new brick hits something immediately (Game Over)
     */
    @Override
    public boolean createNewBrick() {
        // 1. Get the next brick from the front of the queue
        Brick nextBrick = nextBricks.poll();

        // 2. Add a NEW random brick to the end of the queue (keeping size at 3)
        nextBricks.add(brickGenerator.getBrick());

        // 3. Set current brick
        currentBrick.setBrick(nextBrick);

        // Refactor: Use constants instead of magic numbers
        currentOffset = new Point(SPAWN_COL, SPAWN_ROW);

        canHold = true; // Feature: Reset hold permission on new spawn

        return MatrixOperations.intersect(currentGameMatrix, currentBrick.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        int[][] holdShape = (holdBrick == null) ? null : holdBrick.getShapeMatrix().get(0);

        // Feature: Convert the Queue of Bricks into a List of Shapes (int[][]) for the view
        List<int[][]> nextShapes = new ArrayList<>();
        for (Brick b : nextBricks) {
            nextShapes.add(b.getShapeMatrix().get(0));
        }

        return new ViewData(
                currentBrick.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                nextShapes, // Feature: Pass the list of 3 shapes!
                holdShape, // Feature: Pass hold data to view
                getShadowY() // Feature: Pass the calculated shadow Y position
        );
    }

    /**
     * Calculates the Y coordinate where the current brick would land if dropped.
     * Used for the Ghost Piece/Shadow feature.
     */
    private int getShadowY() {
        int shadowY = (int) currentOffset.getY();
        // Keep moving down until we hit something
        while (!MatrixOperations.intersect(currentGameMatrix, currentBrick.getCurrentShape(), (int) currentOffset.getX(), shadowY + 1)) {
            shadowY++;
        }
        return shadowY;
    }

    // Feature: Hold Functionality
    // Swaps the current brick with the held brick
    public boolean holdBrick() {
        if (!canHold) return false;

        if (holdBrick == null) {
            // No brick held yet: Save current, spawn next
            holdBrick = currentBrick.getBrick();
            createNewBrick();
        } else {
            // Swap current with held
            Brick temp = currentBrick.getBrick();
            currentBrick.setBrick(holdBrick);
            holdBrick = temp;
            currentOffset = new Point(SPAWN_COL, SPAWN_ROW);
        }

        canHold = false; // Disable holding until next spawn
        return true;
    }

    /**
     * Locks the current brick into the background grid when it lands.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, currentBrick.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Checks for full rows to clear and updates the board.
     * @return Info about cleared rows and score
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public Score getScore() {
        return score;
    }

    /**
     * Resets the board and score for a new game.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[rows][cols];
        score.reset();
        holdBrick = null; // Feature: Reset hold

        // Reset the queue for a fresh game
        nextBricks.clear();
        for (int i = 0; i < 3; i++) {
            nextBricks.add(brickGenerator.getBrick());
        }

        createNewBrick();
    }
}