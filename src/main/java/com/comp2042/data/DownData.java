package com.comp2042.data;

/**
 * Holds the result of a "Move Down" action.
 * Contains information about whether rows were cleared and the new state of the board.
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the information about cleared rows (if any).
     * @return The ClearRow object, or null if no rows were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the updated view of the board.
     * @return The ViewData object
     */
    public ViewData getViewData() {
        return viewData;
    }
}