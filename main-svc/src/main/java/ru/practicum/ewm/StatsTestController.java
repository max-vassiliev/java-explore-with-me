package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;


/*
 *  Тестовый класс.
 *  Слушает порт 8080 и перенаправляет запросы на порт 9090.
 *  Цель: поверить взаимодействие с сервером статистики (stats-server)
 *
 * */


@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class StatsTestController {

    private final StatsClient statsClient;

    @PostMapping("/hit")
    public Mono<EndpointHitDto> saveHit(@RequestBody EndpointHitDto hitDto) {
        log.info("POST /hit | hitDto: {}", hitDto);
        return statsClient.postEndpointHit(hitDto);
    }

    @GetMapping("/stats")
    public Flux<ViewStatsDto> getViewStats(
            @RequestParam("start") @DateTimeFormat(pattern = EWM_DATE_TIME_FORMAT) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = EWM_DATE_TIME_FORMAT) LocalDateTime end,
            @RequestParam(value = "uris", required = false) String[] uris,
            @RequestParam(name = "unique", required = false) Boolean unique) {
        log.info("GET /stats | start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsClient.getViewStats(start, end, uris, unique);
    }
}
