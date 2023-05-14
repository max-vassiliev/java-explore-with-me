package ru.practicum.ewm.rating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.service.RatingService;

@RestController
@RequestMapping(path = "/users/{userId}/events/{eventId}/ratings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto create(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestParam Boolean like) {
        log.info("POST /users/{}/events/{}/ratings?like={}", userId, eventId, like);
        return ratingService.create(new RatingDto(userId, eventId, like));
    }

    @GetMapping
    public RatingDto getByUserIdAndEventId(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}/ratings", userId, eventId);
        return ratingService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping
    public RatingDto update(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestParam Boolean like) {
        log.info("PATCH /users/{}/events/{}/ratings?like={}", userId, eventId, like);
        return ratingService.update(new RatingDto(userId, eventId, like));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long eventId) {
        log.info("DELETE /users/{}/events/{}/ratings", userId, eventId);
        ratingService.delete(userId, eventId);
    }
}
