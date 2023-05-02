package ru.practicum.ewm.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.dto.update.UpdateEventAdminRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.dto.enums.EventActionAdmin;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.params.EventAdminSearchParams;
import ru.practicum.ewm.event.repository.CustomEventRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.event.service.AdminEventService;
import ru.practicum.ewm.event.service.common.EventServiceUtils;
import ru.practicum.ewm.exception.model.EntityNotFoundException;
import ru.practicum.ewm.exception.model.ValidationException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;

    private final CustomEventRepository customEventRepository;

    private final LocationRepository locationRepository;

    private final CategoryRepository categoryRepository;

    private final EventMapper eventMapper;

    private final LocationMapper locationMapper;

    private final EventServiceUtils eventServiceUtils;

    @Override
    public List<EventFullDto> getAll(EventAdminSearchParams params) {
        List<Event> events = customEventRepository.getAllAdmin(params);
        if (events.isEmpty()) return Collections.emptyList();

        return events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateDto) {
        Event event = getEvent(eventId);
        validateBeforeUpdate(event);
        updateEvent(event, updateDto);
        return eventMapper.toEventFullDto(event);
    }

    // ------------------------------------------
    // Вспомогательные методы (обращение к БД)
    // ------------------------------------------

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d was not found", id),
                        Event.class,
                        LocalDateTime.now())
                );
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Category with id=%d was not found", id),
                        Category.class,
                        LocalDateTime.now())
                );
    }

    private Location saveLocation(LocationDto dto) {
        Location location = locationMapper.toLocation(dto);
        return locationRepository.save(location);
    }

    private void updateEvent(Event event, UpdateEventAdminRequest dto) {
        eventServiceUtils.updateEventFields(event, dto, true);
        if (dto.getCategory() != null) {
            Category category = getCategory(dto.getCategory());
            event.setCategory(category);
        }
        if (dto.getLocation() != null) {
            Location location = saveLocation(dto.getLocation());
            event.setLocation(location);
        }
        if (dto.getStateAction() != null) {
            if (EventActionAdmin.PUBLISH_EVENT.equals(dto.getStateAction())) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (EventActionAdmin.REJECT_EVENT.equals(dto.getStateAction())) {
                event.setState(EventState.CANCELED);
            }
        }
        eventRepository.flush();
    }

    // ------------
    // Валидация
    // ------------

    private void validateBeforeUpdate(Event event) {
        if (!EventState.isStatePending(event)) {
            throw new ValidationException("Cannot publish the event " +
                    "because it's not in the right state: " + event.getState(),
                    LocalDateTime.now());
        }
        eventServiceUtils.validateDateBeforeUpdate(event);
    }
}
