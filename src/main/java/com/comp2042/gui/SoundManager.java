package com.comp2042.gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * Manages all audio playback and mute controls for the game.
 * This class was extracted from GuiController to adhere to the Single Responsibility Principle.
 */
public class SoundManager {

    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;
    private final Button muteButton;

    /**
     * Initializes the SoundManager and starts background music.
     * @param muteButton The button used to display and toggle mute status.
     */
    public SoundManager(Button muteButton) {
        this.muteButton = muteButton;
        initMusic();
    }

    private void initMusic() {
        try {
            // Looks for src/main/resources/music.mp3
            URL musicResource = getClass().getClassLoader().getResource("music.mp3");
            if (musicResource != null) {
                Media sound = new Media(musicResource.toExternalForm());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
                mediaPlayer.setVolume(0.5);
                mediaPlayer.play();
            } else {
                System.out.println("Music file not found. Continuing without audio.");
            }
        } catch (Exception e) {
            System.out.println("Music init failed: " + e.getMessage());
        }
    }

    /** Toggles mute status and updates the button label/style. */
    public void toggleMute(ActionEvent event) {
        if (mediaPlayer == null) return;

        isMuted = !isMuted;
        if (isMuted) {
            mediaPlayer.setMute(true);
            muteButton.setText("UNMUTE");
            muteButton.setStyle("-fx-base: #555555; -fx-font-size: 14px; -fx-padding: 5 15;");
        } else {
            mediaPlayer.setMute(false);
            muteButton.setText("MUTE");
            muteButton.setStyle("-fx-base: #2A5058; -fx-font-size: 14px; -fx-padding: 5 15;");
        }
    }

    public void playMusic() {
        if (mediaPlayer != null && !isMuted) {
            mediaPlayer.play();
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stopAndRestartMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            if (!isMuted) {
                mediaPlayer.play();
            }
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}