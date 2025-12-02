package com.comp2042.gameLogic;

import com.comp2042.logic.bricks.Brick;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the rotation logic of the CurrentBrick (formerly BrickRotator).
 */
class CurrentBrickTest {

    @Test
    void testRotationCycle() {
        CurrentBrick currentBrick = new CurrentBrick();
        MockBrick mockBrick = new MockBrick();
        currentBrick.setBrick(mockBrick);

        // Initial state should be shape 0
        assertArrayEquals(mockBrick.getShapeMatrix().get(0), currentBrick.getCurrentShape());

        // Rotate once -> Shape 1
        currentBrick.setCurrentShape(1);
        assertArrayEquals(mockBrick.getShapeMatrix().get(1), currentBrick.getCurrentShape());

        // Check next shape logic (should wrap around)
        // If current is 1, next should be 0 (since size is 2)
        assertEquals(0, currentBrick.getNextShape().getPosition());
    }

    // A simple fake brick for testing purposes
    static class MockBrick implements Brick {
        private final List<int[][]> matrix = new ArrayList<>();

        public MockBrick() {
            matrix.add(new int[][]{{1}}); // Shape 0
            matrix.add(new int[][]{{2}}); // Shape 1
        }

        @Override
        public List<int[][]> getShapeMatrix() {
            return matrix;
        }
    }
}