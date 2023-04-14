package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.mapper.ViewStatsMapper;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.StatsRepository;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    private final EndpointHitMapper hitMapper;

    private final ViewStatsMapper statsMapper;

    @Override
    @Transactional
    public EndpointHitDto save(EndpointHitDto dto) {
        EndpointHit hit = hitMapper.toEndpointHit(dto);
        return hitMapper.toDto(statsRepository.save(hit));
    }

    @Override
    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        List<ViewStats> stats;

        if (unique && uris.length > 0) {
            stats = statsRepository.getViewStatsWithUniqueIp(uris, start, end);
        } else if (unique) {
            stats = statsRepository.getViewStatsWithUniqueIpWithoutUris(start, end);
        } else if (uris.length == 0) {
            stats = statsRepository.getViewStatsWithoutUris(start, end);
        } else {
            stats = statsRepository.getViewStats(uris, start, end);
        }

        return stats.stream()
                .map(statsMapper::toDto)
                .collect(Collectors.toList());
    }
}
