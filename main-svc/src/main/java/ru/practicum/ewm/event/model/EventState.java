package ru.practicum.ewm.event.model;

public enum EventState {

    PENDING,
    PUBLISHED,
    CANCELED;

    public static boolean isStatePendingOrCancelled(Event event) {
        return PENDING.equals(event.getState()) || CANCELED.equals(event.getState());
    }

    public static boolean isStatePending(Event event) {
        return PENDING.equals(event.getState());
    }

    public static boolean isStatePublished(Event event) {
        return PUBLISHED.equals(event.getState());
    }
}
