package com.comp2042.gameLogic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the player's score, level, and lines cleared.
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty lines = new SimpleIntegerProperty(0);

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public IntegerProperty linesProperty() {
        return lines;
    }

    /**
     * Adds points to the current score.
     * @param i The amount of points to add
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Adds cleared lines and updates the level.
     * Level increases every 10 lines.
     * @param count Number of lines cleared
     */
    public void addLines(int count) {
        if (count > 0) {
            int currentLines = lines.getValue() + count;
            lines.setValue(currentLines);
            // Calculate level: 1 + (Total Lines / 10)
            // Example: 25 lines = Level 3
            level.setValue(1 + (currentLines / 10));
        }
    }

    /**
     * Resets all stats for a new game.
     */
    public void reset() {
        score.setValue(0);
        lines.setValue(0);
        level.setValue(1);
    }
}