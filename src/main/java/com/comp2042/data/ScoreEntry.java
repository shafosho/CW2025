package com.comp2042.data;

import java.io.Serializable;

/**
 * Represents a single high score entry (Name + Score).
 * Implements Comparable to make sorting easy (highest score first).
 */
public class ScoreEntry implements Comparable<ScoreEntry>, Serializable {
    private final String name;
    private final int score;

    public ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(ScoreEntry other) {
        // Sort descending (highest score first)
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return name + " - " + score;
    }
}