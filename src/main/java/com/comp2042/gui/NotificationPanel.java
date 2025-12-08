package com.comp2042.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition; // Added for Pop effect
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class NotificationPanel extends BorderPane {

    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);

        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        score.setTextFill(Color.WHITE);
        score.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

        // Stronger Glow for the "Neon" look
        final Effect glow = new Glow(0.8);
        score.setEffect(glow);

        setCenter(score);
    }

    public void showScore(ObservableList<Node> list) {
        // 1. Fade Out
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        ft.setFromValue(1);
        ft.setToValue(0);

        // 2. Float Up
        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), this);
        tt.setToY(this.getLayoutY() - 60); // Float higher

        // 3. Feature: Pop Effect (Scale Up)
        ScaleTransition st = new ScaleTransition(Duration.millis(500), this);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setCycleCount(1);
        st.setAutoReverse(false);

        // Play all animations together
        ParallelTransition transition = new ParallelTransition(tt, ft, st);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}