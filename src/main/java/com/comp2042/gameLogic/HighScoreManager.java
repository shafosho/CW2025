package com.comp2042.gameLogic;

import com.comp2042.data.ScoreEntry;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages saving and loading high scores to a file.
 */
public class HighScoreManager {

    private static final String FILE_NAME = "highscores.dat";
    private final List<ScoreEntry> scores;

    public HighScoreManager() {
        scores = new ArrayList<>();
        loadScores();
    }

    public void addScore(String name, int score) {
        scores.add(new ScoreEntry(name, score));
        Collections.sort(scores);

        // Changed to Top 3 (was 5)
        if (scores.size() > 3) {
            scores.remove(scores.size() - 1);
        }
        saveScores();
    }

    public List<ScoreEntry> getTopScores() {
        return scores;
    }

    /**
     * Checks if a score qualifies for the top 3 list.
     * Used to decide whether to prompt the user for their name.
     * @param newScore The score to check
     * @return true if the score belongs in the top list, false otherwise.
     */
    public boolean canEnterHighScore(int newScore) {
        // Condition 1: Score must be greater than 0
        if (newScore <= 0) {
            return false;
        }

        // Condition 2: If we have fewer than 3 scores, any score > 0 qualifies
        if (scores.size() < 3) {
            return true;
        }

        // Condition 3: Compare against the lowest score in the list (the last one)
        ScoreEntry lowestEntry = scores.get(scores.size() - 1);
        return newScore > lowestEntry.getScore();
    }

    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            System.err.println("Could not save high scores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<ScoreEntry> loaded = (List<ScoreEntry>) ois.readObject();
            scores.addAll(loaded);
            Collections.sort(scores);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Could not load high scores: " + e.getMessage());
        }
    }
}