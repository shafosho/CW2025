package com.comp2042.gameLogic;

import com.comp2042.data.DownData;
import com.comp2042.data.MoveEvent;
import com.comp2042.data.ViewData;
import com.comp2042.gui.GuiController;

/**
 * The main controller that connects the Game Logic (Board) with the User Interface (GuiController).
 * It handles input events and updates the game state.
 */
public class GameController implements InputEventListener {

    // Refactor: Use constants for board size instead of magic numbers 25 and 10
    private static final int BOARD_HEIGHT = 25;
    private static final int BOARD_WIDTH = 10;

    private final Board board = new SimpleBoard(BOARD_HEIGHT, BOARD_WIDTH);
    private final GuiController viewGuiController;

    /**
     * Creates a new game controller and initializes the board view.
     * @param c The GUI controller to update
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    /**
     * Handles the "Down" key press or automatic gravity movement.
     * @param event The move event details
     * @return Data needed to update the view (cleared rows, new brick position)
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }
        // Note: Soft-drop bonus logic was removed here to balance scoring.

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Resets the game state to start a new round.
     */
    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}