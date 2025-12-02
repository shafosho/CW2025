package com.comp2042.gameLogic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for matrix calculations like checking collisions and merging bricks.
 */
public class MatrixOperations {

    // Refactor: Replaced magic number '50' with a named constant
    private static final int SCORE_PER_LINE = 50;

    private MatrixOperations() {
    }

    /**
     * Checks if the brick hits any filled spots on the board or goes out of bounds.
     * * @param matrix The main game board
     * @param brick The falling brick shape
     * @param x The column position (horizontal)
     * @param y The row position (vertical)
     * @return true if there is a collision or it's out of bounds, false otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {

                // Fixed: j is x (column), i is y (row)
                int targetX = x + j;
                int targetY = y + i;

                if (brick[i][j] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        if (targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length) {
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Makes a totally new copy of the grid so we don't mess up the old one.
     * @param original The 2D array we want to copy
     * @return The new copy of the array
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges the brick into the game board when it lands.
     * * @param filledFields The current game board
     * @param brick The brick to add
     * @param x The column position
     * @param y The row position
     * @return A new board matrix with the brick added
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {

                // Fixed: j is x (column), i is y (row)
                int targetX = x + j;
                int targetY = y + i;

                if (brick[i][j] != 0) {
                    copy[targetY][targetX] = brick[i][j];
                }
            }
        }
        return copy;
    }

    /**
     * Checks for full rows and removes them, shifting everything down.
     * * @param matrix The game board
     * @return A ClearRow object containing the new board and score info
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }

        // Refactor: Use constant 'SCORE_PER_LINE' instead of 50
        int scoreBonus = SCORE_PER_LINE * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}