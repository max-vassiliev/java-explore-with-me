package ru.practicum.ewm.rating.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventInitiatorRatingFull {

    private Long eventId;

    private Long initiatorId;

    private Long eventLikes;

    private Long eventDislikes;

    private Float eventRating;

    private Float initiatorRating;

}
