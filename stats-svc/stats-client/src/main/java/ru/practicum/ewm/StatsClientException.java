package ru.practicum.ewm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatsClientException extends RuntimeException {

    public StatsClientException(String message) {
        super(message);
    }

}
