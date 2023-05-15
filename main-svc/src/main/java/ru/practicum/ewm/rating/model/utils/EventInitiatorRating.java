package ru.practicum.ewm.rating.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventInitiatorRating {

    private Long eventId;

    private Long initiatorId;

    private Float rating;

}
