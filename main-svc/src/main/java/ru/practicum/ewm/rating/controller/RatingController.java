package ru.practicum.ewm.rating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.service.RatingService;

@RestController
@RequestMapping(path = "/users/{userId}/events/{eventId}/ratings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingDto> create(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestParam Boolean like) {
        log.info("POST /users/{}/events/{}/ratings?like={}", userId, eventId, like);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ratingService.create(new RatingDto(userId, eventId, like)));
    }

    @GetMapping
    public ResponseEntity<RatingDto> getByUserIdAndEventId(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}/ratings", userId, eventId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ratingService.getByUserIdAndEventId(userId, eventId));
    }

    @PatchMapping
    public ResponseEntity<RatingDto> update(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestParam Boolean like) {
        log.info("PATCH /users/{}/events/{}/ratings?like={}", userId, eventId, like);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ratingService.update(new RatingDto(userId, eventId, like)));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long eventId) {
        log.info("DELETE /users/{}/events/{}/ratings", userId, eventId);
        ratingService.delete(userId, eventId);
    }
}
