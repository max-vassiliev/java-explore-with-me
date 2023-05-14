package ru.practicum.ewm.rating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.EntityNotFoundException;
import ru.practicum.ewm.exception.model.ValidationException;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.mapper.RatingMapper;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.rating.repository.RatingRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingServiceImpl implements RatingService {

    private static final String EWM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final RatingRepository ratingRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final RatingMapper ratingMapper;

    @Override
    @Transactional
    public RatingDto create(RatingDto dto) {
        User user = getUser(dto.getUserId());
        Event event = getEvent(dto.getEventId());
        validateBeforeSave(user, event);
        Rating rating = ratingMapper.toRating(dto);
        return ratingMapper.toRatingDto(ratingRepository.save(rating));
    }

    @Override
    public RatingDto getByUserIdAndEventId(Long userId, Long eventId) {
        return ratingMapper.toRatingDto(getRating(userId, eventId));
    }

    @Override
    @Transactional
    public RatingDto update(RatingDto dto) {
        Rating rating = getRating(dto.getUserId(), dto.getEventId());
        if (!Objects.equals(dto.getReaction(), rating.getReaction())) {
            rating.setReaction(dto.getReaction());
            ratingRepository.flush();
        }
        return ratingMapper.toRatingDto(rating);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long eventId) {
        Rating rating = getRating(userId, eventId);
        ratingRepository.delete(rating);
    }

    // ---------------------------------------
    // Вспомогательные методы (обращение к БД)
    // ---------------------------------------

    private Rating getRating(Long userId, Long eventId) {
        return ratingRepository.getByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Rating not found for user ID = %d and event ID = %d", userId, eventId),
                        Rating.class,
                        LocalDateTime.now()
                ));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with id=%d was not found", id),
                        User.class,
                        LocalDateTime.now()
                ));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d was not found", id),
                        Event.class,
                        LocalDateTime.now()
                ));
    }

    private Request getRequest(Long userId, Long eventId) {
        return requestRepository.getByRequester_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("No request found for User ID = %d and Event ID = %d", userId, eventId),
                        Request.class,
                        LocalDateTime.now()
                ));
    }

    // -----------
    // Валидация
    // -----------

    private void validateBeforeSave(User user, Event event) {
        validateEventIsPublished(event);
        validateEventIsPast(event);
        validateUserNotInitiator(user, event);
        validateUserRequest(user, event);
    }

    private void validateEventIsPublished(Event event) {
        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new EntityNotFoundException(
                    String.format("Event with id=%d has not been published", event.getId()),
                    Event.class, LocalDateTime.now()
            );
        }
    }

    private void validateEventIsPast(Event event) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(event.getEventDate())) {
            throw new ValidationException(
                    String.format("This event has not yet started. Due to start %s",
                            event.getEventDate().format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))),
                    now
            );
        }
    }

    private void validateUserNotInitiator(User user, Event event) {
        if (Objects.equals(user.getId(), event.getInitiator().getId())) {
            throw new ValidationException(
                    String.format("User with ID=%d is the initiator", user.getId()),
                    LocalDateTime.now()
            );
        }
    }

    private void validateUserRequest(User user, Event event) {
        if (!event.isRequestModeration()) return;
        Request request = getRequest(user.getId(), event.getId());
        if (!RequestStatus.CONFIRMED.equals(request.getStatus())) {
            throw new ValidationException(
                    "Only confirmed participants can rate this event",
                    LocalDateTime.now()
            );
        }
    }
}
