package com.comp2042.data;

/**
 * Represents a request to move the active brick.
 * Carries details about what kind of move (eventType) it is and who triggered it (eventSource).
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Creates a new move event.
     * @param eventType The type of movement (e.g., DOWN, LEFT)
     * @param eventSource The source of the event (USER or THREAD)
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}