package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Validated
public class StatsController {

    private static final String EWM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto create(@RequestBody EndpointHitDto hitDto) {
        log.info("POST /hit | hitDto: {}", hitDto);
        return statsService.save(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getViewStats(
            @RequestParam("start") @DateTimeFormat(pattern = EWM_DATE_TIME_FORMAT) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = EWM_DATE_TIME_FORMAT) LocalDateTime end,
            @RequestParam(value = "uris", required = false, defaultValue = "") String[] uris,
            @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        log.info("GET /stats | start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsService.getViewStats(start, end, uris, unique);
    }
}
