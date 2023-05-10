package ru.practicum.ewm.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.rating.model.Reaction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RatingDto {

    private Long id;
    private Long userId;
    private Long eventId;
    private Reaction reaction;

    public RatingDto(Long userId, Long eventId, boolean like) {
        this.userId = userId;
        this.eventId = eventId;
        if (like) {
            this.reaction = Reaction.LIKE;
        } else {
            this.reaction = Reaction.DISLIKE;
        }
    }
}
