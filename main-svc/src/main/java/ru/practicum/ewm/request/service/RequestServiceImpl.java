package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.EntityNotFoundException;
import ru.practicum.ewm.exception.model.ValidationException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestMapper requestMapper;


    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        validateBeforeCreate(user, event);
        Request request = buildRequest(user, event);
        Request savedRequest = requestRepository.save(request);
        addConfirmedRequest(event);
        return requestMapper.toParticipationRequestDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllByUserId(Long userId) {
        User user = getUser(userId);
        List<Request> requests = requestRepository.getAllByRequester_Id(user.getId());

        if (requests.isEmpty()) return Collections.emptyList();
        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequestByRequestor(Long requestId, Long userId) {
        User user = getUser(userId);
        Request request = getRequest(requestId);
        validateBeforeCancel(request, user);
        removeFromEvent(request);
        setStatusCancel(request);
        return requestMapper.toParticipationRequestDto(request);
    }

    // ------------------------------------------
    // Вспомогательные методы (обращение к БД)
    // ------------------------------------------

    private Request getRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Request with id=%d was not found", id),
                        Request.class,
                        LocalDateTime.now())
                );
    }

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

    private void setStatusCancel(Request request) {
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.flush();
    }

    private void addConfirmedRequest(Event event) {
        if (event.isRequestModeration()) return;
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.flush();
    }

    private void removeFromEvent(Request request) {
        if (!RequestStatus.CONFIRMED.equals(request.getStatus())) return;
        Event event = request.getEvent();
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        eventRepository.flush();
    }

    // ------------------------
    // Вспомогательные методы
    // ------------------------

    private Request buildRequest(User user, Event event) {
        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        if (event.isRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return request;
    }

    // ------------
    // Валидация
    // ------------

    private void validateBeforeCreate(User user, Event event) {
        validateEventIsPublished(event);
        validateUserIsNotInitiator(user, event);
        validateParticipantLimit(event);
    }

    private void validateBeforeCancel(Request request, User user) {
        validateStatusNotCanceled(request);
        validateUserIsRequestor(request, user);
    }

    private void validateStatusNotCanceled(Request request) {
        if (RequestStatus.CANCELED.equals(request.getStatus())) {
            throw new ValidationException(
                    String.format("Request id=%d already cancelled", request.getId()),
                    LocalDateTime.now()
            );
        }
    }

    private void validateUserIsRequestor(Request request, User user) {
        if (!user.getId().equals(request.getRequester().getId())) {
            throw new EntityNotFoundException(
                    String.format("Request with id=%d was not found", request.getId()),
                    Request.class,
                    LocalDateTime.now()
            );
        }
    }

    private void validateUserIsNotInitiator(User user, Event event) {
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ValidationException(String.format("User id=%d initiated this event", user.getId()),
                    LocalDateTime.now());
        }
    }

    private void validateEventIsPublished(Event event) {
        if (!EventState.isStatePublished(event)) {
            throw new ValidationException("This event is not published. " +
                    "Event State is" + event.getState(),
                    LocalDateTime.now());
        }
    }

    private void validateParticipantLimit(Event event) {
        if (event.getParticipantLimit() == 0) {
            return;
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ValidationException(
                    String.format("Exceeded the number of event participants: %d", event.getParticipantLimit()),
                    LocalDateTime.now()
            );
        }
    }
}
