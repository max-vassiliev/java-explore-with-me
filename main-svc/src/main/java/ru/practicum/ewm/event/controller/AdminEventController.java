package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.update.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.params.EventAdminSearchParams;
import ru.practicum.ewm.event.service.AdminEventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class AdminEventController {

    private final AdminEventService eventService;

    @GetMapping
    public List<EventFullDto> getAll(@RequestParam(name = "users", required = false) List<Long> users,
                    @RequestParam(name = "states", required = false) List<EventState> states,
                    @RequestParam(name = "categories", required = false) List<Long> categories,
                    @RequestParam(name = "rangeStart", required = false) String rangeStart,
                    @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET /admin/events?users={}&states={}&categories={}&rangeStart={}&rangeEnd={}" +
                "&from={}&size={}", users, states, categories, rangeStart, rangeEnd, from, size);
        EventAdminSearchParams params = new EventAdminSearchParams(users, states, categories,
                rangeStart, rangeEnd, from, size);
        return eventService.getAll(params);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                                               @Valid @RequestBody UpdateEventAdminRequest updateDto) {
        log.info("PATCH /admin/events/{} | Request Body: {}", eventId, updateDto);
        return eventService.update(eventId, updateDto);
    }
}
