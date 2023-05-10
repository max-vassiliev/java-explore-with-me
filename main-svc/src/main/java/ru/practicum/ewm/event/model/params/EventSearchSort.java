package ru.practicum.ewm.event.model.params;

import ru.practicum.ewm.exception.model.EventSearchSortException;

public enum EventSearchSort {

    EVENT_DATE,
    VIEWS,
    EVENT_RATING,
    INITIATOR_RATING;

    public static EventSearchSort fromString(String string) {
        try {
            return EventSearchSort.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new EventSearchSortException("Unknown state: " + string);
        }
    }
}
