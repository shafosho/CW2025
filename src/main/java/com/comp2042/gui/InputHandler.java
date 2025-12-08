package com.comp2042.gui;

import com.comp2042.data.DownData;
import com.comp2042.data.EventSource;
import com.comp2042.data.EventType;
import com.comp2042.data.MoveEvent;
import com.comp2042.gameLogic.InputEventListener;
import javafx.beans.property.BooleanProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles all user keyboard input for the Tetris game.
 * This class was extracted from GuiController to reduce line count and complexity.
 */
public class InputHandler {

    private InputEventListener eventListener;
    private final GuiController controller;
    private final BooleanProperty isPause;
    private final BooleanProperty isGameOver;

    /**
     * Constructor used during initialization.
     * The event listener is set via the setEventListener method later.
     */
    public InputHandler(GuiController controller, BooleanProperty isPause, BooleanProperty isGameOver) {
        this.controller = controller;
        this.isPause = isPause;
        this.isGameOver = isGameOver;
    }

    /**
     * Sets the event listener that points back to the GameController logic.
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Processes keyboard events from the main game panel.
     * This method contains the core movement and action logic.
     *
     * @param keyEvent The KeyEvent triggered by the user.
     */
    public void handleKeyInput(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.P) {
            controller.togglePause();
            keyEvent.consume();
            return;
        }

        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                controller.refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                controller.refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                controller.refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                controller.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.SPACE) {
                // 1. Capture the full data (Score + Visuals)
                DownData downData = eventListener.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));

                // 2. Check for Score/Line Clears (Logic kept here as it directly uses DownData)
                if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                    // Note: NotificationPanel is assumed to be accessible in GuiController methods
                    // We must pass the data back to GuiController to trigger the NotificationPanel
                    controller.showScoreNotification(downData.getClearRow().getScoreBonus());
                }

                // 3. Refresh the board
                controller.refreshBrick(downData.getViewData());
                keyEvent.consume();
            }
            // Feature: Hold Brick on 'C' Key
            if (keyEvent.getCode() == KeyCode.C) {
                controller.refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
                keyEvent.consume();
            }
        }
        if (keyEvent.getCode() == KeyCode.N) {
            controller.newGame(null);
        }
    }
}