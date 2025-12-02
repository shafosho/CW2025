package com.comp2042.data;

/**
 * Defines who initiated a move event.
 * Used to determine if the player gets points (only USER events award points).
 */
public enum EventSource {
    USER,   // The player pressed a key
    THREAD  // The game timer (gravity) caused the move
}