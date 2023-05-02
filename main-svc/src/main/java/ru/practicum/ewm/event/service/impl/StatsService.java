package ru.practicum.ewm.event.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class StatsService {

    private final StatsClient statsClient;

    @Transactional
    public void saveHit(EndpointHitDto dto) {
        ResponseEntity<Object> result = statsClient.postEndpointHit(dto);
        if (!HttpStatus.CREATED.equals(result.getStatusCode())) {
            log.warn("Stats not saved for EndpointHit: {}", dto);
        }
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris,
                                           Boolean unique) {
        return statsClient.getViewStats(start, end, uris, unique)
                .collectList().block();
    }
}
