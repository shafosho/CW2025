package com.comp2042.gameLogic;

import com.comp2042.logic.bricks.Brick;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CurrentBrickTest {

    @Test
    void testRotationCycle() {
        CurrentBrick currentBrick = new CurrentBrick();
        MockBrick mockBrick = new MockBrick();
        currentBrick.setBrick(mockBrick);

        assertArrayEquals(mockBrick.getShapeMatrix().get(0), currentBrick.getCurrentShape());

        currentBrick.setCurrentShape(1);
        assertArrayEquals(mockBrick.getShapeMatrix().get(1), currentBrick.getCurrentShape());

        assertEquals(0, currentBrick.getNextShape().getPosition());
    }

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