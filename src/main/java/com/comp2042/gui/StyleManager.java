package com.comp2042.gui;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Handles all visual styling functions, including the block color palette
 * and applying basic styling (like rounded corners) to Tetris elements.
 * Extracted from GuiController for SRP adherence.
 */
public class StyleManager {
    public StyleManager() {
    }

    /**
     * Cohesive "Cyberpunk" Palette for Midnight Theme.
     * @param i The color ID (1-7) corresponding to the block type.
     * @return The JavaFX Paint object for the corresponding color.
     */
    public Paint getFillColor(int i) {
        switch (i) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.web("#00f0ff"); // Electric Cyan
            case 2: return Color.web("#ff0099"); // Hot Pink
            case 3: return Color.web("#39ff14"); // Neon Lime
            case 4: return Color.web("#fff01f"); // Sunshine Yellow
            case 5: return Color.web("#ff4500"); // Bright Orange-Red
            case 6: return Color.web("#bd00ff"); // Electric Purple
            case 7: return Color.web("#ff9900"); // Tangerine Orange
            default: return Color.WHITE;
        }
    }

    /**
     * Applies the fill color and standard block styling (rounded corners) to a rectangle.
     * @param color The color ID.
     * @param rectangle The target Rectangle object.
     */
    public void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    /**
     * Applies high score style to labels.
     * @param highScoreLabel The label showing the score value.
     * @param highScoreNameLabel The label showing the score name.
     */
    public void applyHighScoreStyle(Label highScoreLabel, Label highScoreNameLabel) {
        highScoreLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 24px;");
        highScoreNameLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 20px;");
    }

    /**
     * Reverts high score style to default (e.g., when a new game starts or score drops).
     * @param highScoreLabel The label showing the score value.
     * @param highScoreNameLabel The label showing the score name.
     */
    public void revertHighScoreStyle(Label highScoreLabel, Label highScoreNameLabel) {
        // Assuming default style is handled by CSS, we only need to remove the overrides.
        highScoreLabel.setStyle(null);
        highScoreNameLabel.setStyle(null);
    }
}