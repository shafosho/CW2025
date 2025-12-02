package com.comp2042.logic.bricks;

import com.comp2042.gameLogic.MatrixOperations;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all bricks.
 * Stores the matrix data so individual bricks don't have to duplicate this logic.
 */
public abstract class BrickBase implements Brick {

    protected final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Shared logic to get the shape matrix.
     * Prevents code duplication in every single brick class.
     * @return A deep copy of the brick's matrix
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}