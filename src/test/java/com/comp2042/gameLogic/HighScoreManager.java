package com.comp2042.gameLogic;

import com.comp2042.data.ScoreEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {

    private HighScoreManager manager;
    private static final String FILE_NAME = "highscores.dat";

    @BeforeEach
    void setUp() {
        // Clean up any existing file before each test to ensure a fresh start
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
        manager = new HighScoreManager();
    }

    @Test
    void testAddScoreSortsCorrectly() {
        manager.addScore("Player1", 100);
        manager.addScore("Player2", 300);
        manager.addScore("Player3", 200);

        List<ScoreEntry> scores = manager.getTopScores();

        // Should be sorted Descending: 300, 200, 100
        assertEquals(300, scores.get(0).getScore());
        assertEquals(200, scores.get(1).getScore());
        assertEquals(100, scores.get(2).getScore());
    }

    @Test
    void testListCappedAtThree() {
        // Add 5 scores
        manager.addScore("A", 100);
        manager.addScore("B", 200);
        manager.addScore("C", 300);
        manager.addScore("D", 400);
        manager.addScore("E", 500);

        List<ScoreEntry> scores = manager.getTopScores();

        // Should only have 3
        assertEquals(3, scores.size());

        // The top one should be 500, lowest should be 300 (400 and 500 pushed 100/200 out)
        assertEquals(500, scores.get(0).getScore());
        assertEquals(300, scores.get(2).getScore());
    }

    @Test
    void testCanEnterHighScore() {
        // Case 1: Empty list, ANY score > 0 should enter
        assertTrue(manager.canEnterHighScore(50));
        assertFalse(manager.canEnterHighScore(0)); // 0 never enters

        // Fill the list with 100, 200, 300
        manager.addScore("A", 100);
        manager.addScore("B", 200);
        manager.addScore("C", 300);

        // Case 2: List full (Top is 300, Lowest is 100)

        // A score of 50 is lower than 100 -> Should FAIL
        assertFalse(manager.canEnterHighScore(50));

        // A score of 150 is higher than 100 -> Should PASS
        assertTrue(manager.canEnterHighScore(150));
    }
}