package com.comp2042.gameLogic;

import com.comp2042.data.NextShapeInfo;
import com.comp2042.data.ViewData;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

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
        Brick currentBrick = brickGenerator.getBrick();
        this.currentBrick.setBrick(currentBrick);

        // Refactor: Use constants instead of magic numbers
        currentOffset = new Point(SPAWN_COL, SPAWN_ROW);

        return MatrixOperations.intersect(currentGameMatrix, this.currentBrick.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(currentBrick.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
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
        createNewBrick();
    }
}