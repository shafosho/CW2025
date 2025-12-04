package com.comp2042.gameLogic;

import com.comp2042.data.DownData;
import com.comp2042.data.ClearRow;
import com.comp2042.data.MoveEvent;
import com.comp2042.data.ViewData;
import com.comp2042.gui.GuiController;

/**
 * The main controller that connects the Game Logic (Board) with the User Interface (GuiController).
 * It handles input events and updates the game state.
 */
public class GameController implements InputEventListener {

    private static final int BOARD_HEIGHT = 25;
    private static final int BOARD_WIDTH = 10;

    private final Board board = new SimpleBoard(BOARD_HEIGHT, BOARD_WIDTH);
    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());

        // Updated: Bind Score, Level, and Lines to the GUI
        viewGuiController.bindScore(
                board.getScore().scoreProperty(),
                board.getScore().levelProperty(),
                board.getScore().linesProperty()
        );
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                // Feature: Update lines count to check for Level Up
                board.getScore().addLines(clearRow.getLinesRemoved());
            }

            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }

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
     * Handles the Hard Drop event (Space Bar).
     * Moves the brick down repeatedly until it hits something.
     */
    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        boolean canMove = true;

        // Loop until collision (SLAM the brick down)
        while (canMove) {
            canMove = board.moveBrickDown();
        }

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            // Feature: Update lines count to check for Level Up
            board.getScore().addLines(clearRow.getLinesRemoved());
        }

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        // Cast board to SimpleBoard if 'Board' interface doesn't have holdBrick()
        // Or update Board interface. For now, it's casted.
        if (board instanceof SimpleBoard) {
            ((SimpleBoard) board).holdBrick();
        }
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}