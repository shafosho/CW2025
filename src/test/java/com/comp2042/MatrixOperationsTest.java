package com.comp2042;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify the logic in MatrixOperations.
 * Checks for coordinate mapping errors and boundary collisions.
 */
public class MatrixOperationsTest {

    // Simple 4x10 board
    private final int[][] BOARD = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    // T-shaped brick (number 7)
    // 0 7 0
    // 7 7 7
    // 0 0 0
    private final int[][] T_BRICK = new int[][]{
            {0, 7, 0},
            {7, 7, 7},
            {0, 0, 0}
    };

    @Test
    void testDeepCopy() {
        int[][] original = {{1, 2}, {3, 4}};
        int[][] copy = MatrixOperations.copy(original);

        assertArrayEquals(original[0], copy[0]);

        // Make sure changing the copy doesn't change the original
        copy[0][0] = 99;
        assertNotEquals(original[0][0], copy[0][0]);
    }

    @Test
    void testMergeTBrick() {
        // Put the T-brick at x=4, y=1
        int x = 4;
        int y = 1;

        int[][] result = MatrixOperations.merge(BOARD, T_BRICK, x, y);

        // Check if the top part of the T is in the right place
        // It should be at row 1, col 5
        assertEquals(7, result[1][5]);

        // Check the middle bar of the T
        assertEquals(7, result[2][4]);
        assertEquals(7, result[2][5]);
        assertEquals(7, result[2][6]);

        // Check that it didn't flip sideways (this was the bug before)
        assertEquals(0, result[1][4]);
    }

    @Test
    void testIntersectOutOfBounds() {
        int[][] brick = {{1}}; // Single block

        // Check far left
        assertTrue(MatrixOperations.intersect(BOARD, brick, -1, 0));

        // Check far right
        assertTrue(MatrixOperations.intersect(BOARD, brick, 10, 0));

        // Check valid spot
        assertFalse(MatrixOperations.intersect(BOARD, brick, 0, 0));
    }
}