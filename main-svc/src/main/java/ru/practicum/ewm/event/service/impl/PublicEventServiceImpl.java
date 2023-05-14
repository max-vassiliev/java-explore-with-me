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
import ru.practicum.ewm.rating.model.utils.EventInitiatorRatingFull;
import ru.practicum.ewm.rating.model.utils.EventRating;
import ru.practicum.ewm.rating.model.utils.EventInitiatorRating;
import ru.practicum.ewm.rating.repository.EventRatingRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PublicEventServiceImpl implements PublicEventService {

    private static final String EWM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final EventRepository eventRepository;

    private final CustomEventRepository customEventRepository;

    private final EventRatingRepository eventRatingRepository;

    private final StatsService statsService;

    private final EventMapper eventMapper;


    @Override
    @Transactional
    public EventFullDto getById(Long id, HttpServletRequest request) {
        Event event = getPublishedEvent(id);
        statsService.saveHit(createEndpointHit(request));
        countViews(request, event);
        fetchRatings(event);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAll(EventPublicSearchParams params, HttpServletRequest request) {
        List<Event> events = searchEvents(params);
        statsService.saveHit(createEndpointHit(request));
        if (events.isEmpty()) return Collections.emptyList();

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------
    // Вспомогательные методы (обращение к БД)
    // ----------------------------------------

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
        Long views = (long) statsService.getViewStats(event.getCreatedOn(),
                LocalDateTime.now(), uri, null).size();
        event.setViews(views);
        eventRepository.flush();
    }

    private void fetchRatings(Event event) {
        Optional<EventInitiatorRatingFull> optionalRatingData = eventRatingRepository.getFullRatingsByEventId(event.getId());
        if (optionalRatingData.isEmpty()) {
            return;
        }

        EventInitiatorRatingFull ratingData = optionalRatingData.get();
        event.setLikes(ratingData.getEventLikes());
        event.setDislikes(ratingData.getEventDislikes());
        event.setRating(ratingData.getEventRating());
        event.getInitiator().setEventsRating(ratingData.getInitiatorRating());
    }

    private List<Event> searchEvents(EventPublicSearchParams params) {
        List<Event> events;
        switch (params.getSort()) {
            case EVENT_RATING:
                events = searchEventsByEventRating(params);
                break;
            case INITIATOR_RATING:
                events = searchEventsByInitiatorRating(params);
                break;
            default:
                events = searchEventsByDefault(params);
                break;
        }
        return events;
    }

    private List<Event> searchEventsByDefault(EventPublicSearchParams params) {
        List<Event> events = customEventRepository.getAllPublic(params);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        if (params.isFutureEventSearch()) {
            return events;
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<EventRating> eventRatings = eventRatingRepository.getEventRatings(eventIds);
        Map<Long, EventRating> eventRatingMap = eventRatings.stream()
                .collect(Collectors.toMap(EventRating::getEventId, eventRating -> eventRating));
        populateRatings(events, eventRatingMap);

        return events;
    }

    private List<Event> searchEventsByEventRating(EventPublicSearchParams params) {
        List<EventRating> eventRatings = eventRatingRepository.getEventRatings(params);
        if (eventRatings.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, EventRating> eventRatingMap = eventRatings.stream()
                .collect(Collectors.toMap(EventRating::getEventId, eventRating -> eventRating));

        List<Long> eventIds = eventRatings.stream()
                .map(EventRating::getEventId)
                .collect(Collectors.toList());

        List<Event> events = eventRepository.findAllByIdIn(eventIds);
        populateRatings(events, eventRatingMap);
        events.sort(Comparator.comparingLong(event -> eventIds.indexOf(event.getId())));

        return events;
    }

    private List<Event> searchEventsByInitiatorRating(EventPublicSearchParams params) {
        List<EventInitiatorRating> initiatorRatings = eventRatingRepository.getEventInitiatorRatings(params);

        if (initiatorRatings.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, EventInitiatorRating> initiatorRatingMap = initiatorRatings.stream()
                .collect(Collectors.toMap(EventInitiatorRating::getEventId, initiatorRating -> initiatorRating));

        List<Long> eventIds = initiatorRatings.stream()
                .map(EventInitiatorRating::getEventId)
                .collect(Collectors.toList());

        List<Event> events = eventRepository.findAllByIdIn(eventIds);

        if (params.isFutureEventSearch()) {
            events.forEach(event -> event.getInitiator()
                    .setEventsRating(initiatorRatingMap.get(event.getId()).getRating()));
        } else {
            List<EventRating> eventRatings = eventRatingRepository.getEventRatings(eventIds);
            Map<Long, EventRating> eventRatingMap = eventRatings.stream()
                    .collect(Collectors.toMap(EventRating::getEventId, eventRating -> eventRating));
            events.forEach( event -> {
                event.getInitiator().setEventsRating(initiatorRatingMap.get(event.getId()).getRating());
                if (eventRatingMap.containsKey(event.getId())) {
                    event.setLikes(eventRatingMap.get(event.getId()).getLikes());
                    event.setDislikes(eventRatingMap.get(event.getId()).getDislikes());
                    event.setRating(eventRatingMap.get(event.getId()).getRating());
                }
            });
        }

        events.sort(Comparator.comparingLong(event -> eventIds.indexOf(event.getId())));
        return events;
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

    private void populateRatings(List<Event> events, Map<Long, EventRating> eventRatingMap) {
        events.forEach(event -> {
            if (eventRatingMap.containsKey(event.getId())) {
                event.setLikes(eventRatingMap.get(event.getId()).getLikes());
                event.setDislikes(eventRatingMap.get(event.getId()).getDislikes());
                event.setRating(eventRatingMap.get(event.getId()).getRating());
            }
        });
    }
}
