package ru.practicum.ewm.rating.service;

import ru.practicum.ewm.rating.dto.RatingDto;

public interface RatingService {
    RatingDto create(RatingDto dto);

    RatingDto getByUserIdAndEventId(Long userId, Long eventId);

    RatingDto update(RatingDto dto);

    void delete(Long userId, Long eventId);
}
