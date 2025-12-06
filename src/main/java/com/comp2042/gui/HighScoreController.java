package com.comp2042.gui;

import com.comp2042.data.ScoreEntry;
import com.comp2042.gameLogic.HighScoreManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HighScoreController implements Initializable {

    @FXML private Label score1;
    @FXML private Label score2;
    @FXML private Label score3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HighScoreManager manager = new HighScoreManager();
        List<ScoreEntry> topScores = manager.getTopScores();

        // Display top 3 (handles if list is empty)
        if (topScores.size() > 0) score1.setText("1. " + topScores.get(0).getScore() + " - " + topScores.get(0).getName());
        else score1.setText("1. ----");

        if (topScores.size() > 1) score2.setText("2. " + topScores.get(1).getScore() + " - " + topScores.get(1).getName());
        else score2.setText("2. ----");

        if (topScores.size() > 2) score3.setText("3. " + topScores.get(2).getScore() + " - " + topScores.get(2).getName());
        else score3.setText("3. ----");
    }

    @FXML
    public void onBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("startMenuPage.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
        stage.setTitle("TetrisJFX");
        stage.setScene(scene);
        stage.show();
    }
}