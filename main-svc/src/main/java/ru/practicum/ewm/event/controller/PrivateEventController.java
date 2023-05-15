package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.update.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class PrivateEventController {

    private final PrivateEventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                                               @Valid @RequestBody NewEventDto newEvent) {
        log.info("POST /users/{}/events | Body: {}", userId, newEvent);
        return eventService.create(userId, newEvent);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserIdAndEventId(@PathVariable Long userId,
                                                @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventService.getByUserIdAndEventId(eventId, userId);
    }

    @GetMapping
    public List<EventShortDto> getAllByUserId(@PathVariable Long userId,
                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.getAllByUserId(userId,
                new CustomPageRequest(from, size, Sort.by("id")));
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                                          @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return eventService.getRequestsByEventIdAndUserId(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @Valid @RequestBody UpdateEventUserRequest updateDto) {
        log.info("PATCH /users/{}/events/{} | Request Body: {}", userId, eventId, updateDto);
        return eventService.update(eventId, userId, updateDto);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                                              @PathVariable Long eventId,
                                                                              @RequestBody EventRequestStatusUpdateRequest inputDto) {
        log.info("PATCH /users/{}/events/{}/requests | Request Body: {}", userId, eventId, inputDto);
        return eventService.updateRequestStatus(inputDto, eventId, userId);
    }
}
