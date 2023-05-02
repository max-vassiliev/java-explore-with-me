package ru.practicum.ewm.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.update.UpdateEventUserRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.params.CreateEventParams;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.dto.enums.EventActionUser;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.event.service.common.EventServiceUtils;
import ru.practicum.ewm.exception.model.EntityNotFoundException;
import ru.practicum.ewm.exception.model.ValidationException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PrivateEventServiceImpl implements PrivateEventService {

    private static final int MINIMUM_HOURS_BEFORE_EVENT = 2;

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final EventMapper eventMapper;

    private final LocationMapper locationMapper;

    private final RequestMapper requestMapper;

    private final EventServiceUtils eventServiceUtils;


    @Override
    @Transactional
    public EventFullDto create(Long initiatorId, NewEventDto newEventDto) {
        CreateEventParams params = new CreateEventParams();
        fetchEventParams(params, initiatorId, newEventDto);
        Event event = eventMapper.toNewEvent(newEventDto, params);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getByUserIdAndEventId(Long eventId, Long userId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validateEventInitiator(event, user);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllByUserId(Long userId, Pageable pageable) {
        getUser(userId);
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);
        if (events.isEmpty()) return Collections.emptyList();

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventIdAndUserId(Long eventId, Long userId) {
        validateEventInitiator(eventId, userId);
        List<Request> requests = requestRepository.getAllByEvent_Id(eventId);
        if (requests.isEmpty()) return Collections.emptyList();
        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, Long userId, UpdateEventUserRequest updateDto) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validateBeforeUpdate(event, user);
        updateEventFields(event, updateDto);
        eventRepository.flush();
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest inputDto,
                                                              Long eventId,
                                                              Long userId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validateBeforeRequestStatusUpdate(event, user);
        List<Request> requests = processRequestsForStatusUpdate(inputDto.getRequestIds(), eventId);
        return prepareRequestStatusUpdateResult(requests, inputDto.getStatus(), event);
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

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with id=%d was not found", id),
                        User.class,
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

    private List<Request> getRequestsByIds(List<Long> requestIds) {
        return requestRepository.getAllByIdIn(requestIds);
    }

    private EventRequestStatusUpdateResult confirmRequests(List<Request> requests, Event event) {
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        try {
            for (Request request : requests) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(request);
                }
            }
            if (!rejectedRequests.isEmpty()) {
                throw new ValidationException();
            }
        } catch (ValidationException exception) {
            eventRepository.flush();
            requestRepository.flush();
            throw new ValidationException("The participant limit has been reached", LocalDateTime.now());
        }

        eventRepository.flush();
        requestRepository.flush();

        List<ParticipationRequestDto> confirmedRequestsDto = confirmedRequests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequestsDto, Collections.emptyList());
    }

    private EventRequestStatusUpdateResult rejectRequests(List<Request> requests) {
        for (Request request : requests) {
            request.setStatus(RequestStatus.REJECTED);
        }
        requestRepository.flush();

        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(Collections.emptyList(), rejectedRequests);
    }

    // -------------------------
    // Вспомогательные методы
    // -------------------------

    private void fetchEventParams(CreateEventParams eventParams, Long initiatorId, NewEventDto newEventDto) {
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime eventDate = getEventDate(newEventDto.getEventDate(), createdOn);
        User initiator = getUser(initiatorId);
        Category category = getCategory(newEventDto.getCategory());
        Location location = saveLocation(newEventDto.getLocation());

        eventParams.setCreatedOn(createdOn);
        eventParams.setEventDate(eventDate);
        eventParams.setInitiator(initiator);
        eventParams.setCategory(category);
        eventParams.setLocation(location);
    }

    private LocalDateTime getEventDate(String date, LocalDateTime createdOn) {
        LocalDateTime eventDate = LocalDateTime.parse(date,
                DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT));
        validateEventDate(eventDate, createdOn);
        return eventDate;
    }

    private void updateEventFields(Event event, UpdateEventUserRequest dto) {
        eventServiceUtils.updateEventFields(event, dto, false);
        if (dto.getCategory() != null) {
            Category category = getCategory(dto.getCategory());
            event.setCategory(category);
        }
        if (dto.getLocation() != null) {
            Location location = saveLocation(dto.getLocation());
            event.setLocation(location);
        }
        if (dto.getStateAction() != null) {
            if (EventActionUser.SEND_TO_REVIEW.equals(dto.getStateAction())) {
                event.setState(EventState.PENDING);
            }
            if (EventActionUser.CANCEL_REVIEW.equals(dto.getStateAction())) {
                event.setState(EventState.CANCELED);
            }
        }
    }

    private List<Request> processRequestsForStatusUpdate(List<Long> requestIds, Long eventId) {
        List<Request> requests = getRequestsByIds(requestIds);

        List<Request> filteredRequests = requests.stream()
                .filter(request -> Objects.equals(request.getEvent().getId(), eventId))
                .filter(request -> RequestStatus.PENDING.equals(request.getStatus()))
                .collect(Collectors.toList());

        if (requests.size() != filteredRequests.size()) {
            eventServiceUtils.collectErrorsOnRequestUpdate(requests, eventId);
        }
        return filteredRequests;
    }

    private EventRequestStatusUpdateResult prepareRequestStatusUpdateResult(List<Request> requests,
                                                                            RequestStatus newStatus,
                                                                            Event event) {
        EventRequestStatusUpdateResult result;
        switch (newStatus) {
            case CONFIRMED:
                result = confirmRequests(requests, event);
                break;
            case REJECTED:
                result = rejectRequests(requests);
                break;
            default:
                throw new ValidationException(String.format("Status %s not supported", newStatus),
                        LocalDateTime.now());
        }
        return result;
    }


    // ------------
    // Валидация
    // ------------

    private void validateEventDate(LocalDateTime eventDate, LocalDateTime now) {
        if (eventDate.isBefore(now)) {
            throw new ValidationException("Field: eventDate. " +
                    "Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + eventDate.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT)),
                    now);
        }
        if (eventDate.isBefore(now.plusHours(MINIMUM_HOURS_BEFORE_EVENT))) {
            throw new ValidationException("Field: eventDate. " +
                    "Error: событие должно начаться как минимум через два часа. " +
                    "Value: " + eventDate.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT)),
                    now);
        }
    }

    private void validateEventInitiator(Event event, User user) {
        if (!Objects.equals(event.getInitiator().getId(), user.getId())) {
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", event.getId()),
                    Event.class,
                    LocalDateTime.now()
            );
        }
    }

    private void validateEventInitiator(Long eventId, Long userId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validateEventInitiator(event, user);
    }

    private void validateBeforeUpdate(Event event, User user) {
        if (!EventState.isStatePendingOrCancelled(event)) {
            throw new ValidationException("Only pending or canceled events can be changed", LocalDateTime.now());
        }
        eventServiceUtils.validateDateBeforeUpdate(event);
        validateEventInitiator(event, user);
    }

    private void validateBeforeRequestStatusUpdate(Event event, User user) {
        if (Event.isParticipantLimitZeroAndNoModerationRequired(event)) {
            throw new ValidationException("Validation not required for this event.", LocalDateTime.now());
        }
        validateEventInitiator(event, user);
    }
}

