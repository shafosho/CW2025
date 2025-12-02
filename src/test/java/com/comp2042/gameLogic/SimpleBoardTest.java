package com.comp2042.gameLogic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the game board logic to verify dimensions and resets.
 */
public class SimpleBoardTest {

    @Test
    void testBoardDimensions() {
        // Create a board with specific dimensions (Rows=20, Cols=10)
        // This confirms your "rows/cols" refactoring works correctly
        SimpleBoard board = new SimpleBoard(20, 10);
        int[][] matrix = board.getBoardMatrix();

        assertEquals(20, matrix.length, "Board height (rows) should be 20");
        assertEquals(10, matrix[0].length, "Board width (cols) should be 10");
    }

    @Test
    void testNewGameResetsScore() {
        SimpleBoard board = new SimpleBoard(20, 10);
        board.getScore().add(1000);

        // Reset the game
        board.newGame();

        // Verify score is back to 0
        assertEquals(0, board.getScore().scoreProperty().get(), "New Game should reset score to 0");
    }
}