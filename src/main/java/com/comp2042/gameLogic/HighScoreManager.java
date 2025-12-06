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
        // Keep only top 5
        if (scores.size() > 5) {
            scores.remove(scores.size() - 1);
        }
        saveScores();
    }

    public List<ScoreEntry> getTopScores() {
        return scores;
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