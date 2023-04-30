package ru.practicum.ewm.exception.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventSearchSortException extends RuntimeException {

    public EventSearchSortException(String message) {
        super(message);
    }
}
