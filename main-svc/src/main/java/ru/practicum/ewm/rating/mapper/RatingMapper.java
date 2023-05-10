package ru.practicum.ewm.rating.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.user.model.User;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingMapper {

    public Rating toRating(RatingDto dto, User user, Event event) {
        Rating rating = new Rating();
        rating.setReaction(dto.getReaction());
        rating.setUser(user);
        rating.setEvent(event);
        return rating;
    }

    public RatingDto toRatingDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setUserId(rating.getUser().getId());
        dto.setEventId(rating.getEvent().getId());
        dto.setReaction(rating.getReaction());
        return dto;
    }
}
