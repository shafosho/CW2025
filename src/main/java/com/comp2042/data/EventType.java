package com.comp2042.data;

/**
 * Defines the possible actions a brick can take.
 */
public enum EventType {
    DOWN,   // Move down one row
    LEFT,   // Move left one column
    RIGHT,  // Move right one column
    ROTATE, // Rotate 90 degrees
    HARD_DROP // Drop instantly to the bottom
}