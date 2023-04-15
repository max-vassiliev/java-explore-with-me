package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitDto save(EndpointHitDto hitDto);

    List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end,
                                    String[] uris, boolean unique);
}
