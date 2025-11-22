package com.comp2042;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MatrixOperationsTest {

    @Test
    void testIntersect_transposeBug(){

        //2x2 empty board
        int[] board = {
                {0, 0},
                {0, 0}
        };

        //Simple 2x2 brick
        int[][] brick = {
                {1, 1},
                {1, 1}
        };

        //Place brick at (0,0), should not intersect
        boolean result = MatrixOperations.intersect(board, brick, 0, 0);

        //If bug exists ([j][i] used), would return false
        assertFalse(result, "intersect() should return false for empty board");
    }

    @Test
    void testMerge_transposeBug() {

        int[][] board = {
                {0, 0},
                {0, 0}
        };

        int[][] brick = {
                {1, 1},
                {1, 1}
        };

        int[][] merged = MatrixOperations.merge(board, brick, 0, 0);

        int[][] expected = {
                {1, 1},
                {1, 1}
        };

        assertArrayEquals(expected, merged, "merge() should correctly copy brick onto board");
    }
}
