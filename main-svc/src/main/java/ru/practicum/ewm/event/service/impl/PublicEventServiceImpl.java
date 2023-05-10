package ru.practicum.ewm.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.params.EventPublicSearchParams;
import ru.practicum.ewm.event.repository.CustomEventRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.PublicEventService;
import ru.practicum.ewm.exception.model.EntityNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;
import static ru.practicum.ewm.common.StatsConstants.EARLIEST_DATE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;

    private final CustomEventRepository customEventRepository;

    private final StatsService statsService;

    private final EventMapper eventMapper;


    @Override
    @Transactional
    public EventFullDto getById(Long id, HttpServletRequest request) {
        Event event = getPublishedEvent(id);
        statsService.saveHit(createEndpointHit(request));
        countViews(request, event);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAll(EventPublicSearchParams params, HttpServletRequest request) {
        List<Event> events = customEventRepository.getAllPublic(params);
        statsService.saveHit(createEndpointHit(request));
        if (events.isEmpty()) return Collections.emptyList();

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    // ------------------------
    // Вспомогательные методы
    // ------------------------

    private EndpointHitDto createEndpointHit(HttpServletRequest request) {
        EndpointHitDto dto = new EndpointHitDto();
        dto.setApp("main-svc");
        dto.setUri(request.getRequestURI());
        dto.setIp(request.getRemoteAddr());
        dto.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT)));
        return dto;
    }

    private Event getPublishedEvent(Long id) {
        return eventRepository.findByIdAndStateIs(id, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d was not found", id),
                        Event.class,
                        LocalDateTime.now())
                );
    }

    private void countViews(HttpServletRequest request, Event event) {
        List<String> uri = Collections.singletonList(request.getRequestURI());
        Long views = (long) statsService.getViewStats(EARLIEST_DATE,
                LocalDateTime.now(), uri, null).size();
        event.setViews(views);
        eventRepository.flush();
    }
}
