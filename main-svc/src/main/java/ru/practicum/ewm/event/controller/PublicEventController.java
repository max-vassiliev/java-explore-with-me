package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.params.EventPublicSearchParams;
import ru.practicum.ewm.event.service.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class PublicEventController {

    private final PublicEventService eventService;

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable Long id,
                                                HttpServletRequest request) {
        log.info("GET /events/{} | Client IP: {} | Endpoint Path: {}",
                id, request.getRemoteAddr(), request.getRequestURI());
        return eventService.getById(id, request);
    }

    @GetMapping
    public List<EventShortDto> getAll(@RequestParam(name = "text", required = false) String text,
                    @RequestParam(name = "categories", required = false) List<Long> categories,
                    @RequestParam(name = "paid", required = false) Boolean paid,
                    @RequestParam(name = "rangeStart", required = false) String rangeStart,
                    @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                    @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                    @RequestParam(name = "sort", defaultValue = "event_date") String sort,
                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                    @Positive @RequestParam(name = "size", defaultValue = "10") int size,
                    HttpServletRequest request) {
        log.info("GET /events?text={}&categories={}&paid={}&rangeStart={}&rangeEnd={}&onlyAvailable={}" +
                "&sort={}&from={}&size={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size);
        EventPublicSearchParams params = new EventPublicSearchParams(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getAll(params, request);
    }
}
