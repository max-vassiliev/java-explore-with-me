package ru.practicum.ewm.event.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.practicum.ewm.event.dto.update.UpdateEventRequest;
import ru.practicum.ewm.event.mapper.EventMapperLite;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.model.ValidationException;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventServiceUtils {

    private static final int MINIMUM_HOURS_BEFORE_EVENT_ADMIN = 1;

    private static final int MINIMUM_HOURS_BEFORE_EVENT_USER = 2;

    private final EventMapperLite eventMapper;

    public void validateDateBeforeUpdate(Event event) {
        LocalDateTime now = LocalDateTime.now();
        if (event.getEventDate().isBefore(now)) {
            throw new ValidationException("Only future events can be updated", now);
        }
    }

    public void updateEventFields(Event event, UpdateEventRequest dto, boolean isAdmin) {
        if (dto.getEventDate() != null) {
            LocalDateTime eventDate = getEventDate(dto.getEventDate(), LocalDateTime.now(), isAdmin);
            event.setEventDate(eventDate);
        }
        eventMapper.updateFields(dto, event);
    }

    private LocalDateTime getEventDate(String date, LocalDateTime now, boolean isAdmin) {
        LocalDateTime eventDate = LocalDateTime.parse(date,
                DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT));
        validateEventDate(eventDate, now, isAdmin);
        return eventDate;
    }

    private void validateEventDate(LocalDateTime eventDate, LocalDateTime now, boolean isAdmin) {
        if (eventDate.isBefore(now)) {
            throw new ValidationException("Field: eventDate. " +
                    "Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + eventDate.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT)),
                    now);
        }
        if (isAdmin) {
            if (eventDate.isBefore(now.plusHours(MINIMUM_HOURS_BEFORE_EVENT_ADMIN))) {
                throw new ValidationException("Field: eventDate. " +
                        "Error: событие должно начаться как минимум через час. " +
                        "Value: " + eventDate.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT)),
                        now);
            }
        } else {
            if (eventDate.isBefore(now.plusHours(MINIMUM_HOURS_BEFORE_EVENT_USER))) {
                throw new ValidationException("Field: eventDate. " +
                        "Error: событие должно начаться как минимум через час. " +
                        "Value: " + eventDate.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT)),
                        now);
            }
        }
    }

    public void collectErrorsOnRequestUpdate(List<Request> requests, Long eventId) {
        List<Request> wrongEventRequests = requests.stream()
                .filter(request -> !Objects.equals(request.getEvent().getId(), eventId))
                .collect(Collectors.toList());

        List<Request> wrongStatusRequests = requests.stream()
                .filter(request -> !RequestStatus.PENDING.equals(request.getStatus()))
                .collect(Collectors.toList());

        List<Long> wrongRequestsIds = Stream.concat(wrongEventRequests.stream(), wrongStatusRequests.stream())
                .map(Request::getId)
                .distinct()
                .collect(Collectors.toList());

        StringBuilder errorMessageBuilder = new StringBuilder();
        errorMessageBuilder.append("Invalid request IDs: ").append(wrongRequestsIds).append(". ");

        if (!wrongEventRequests.isEmpty()) {
            String error = getErrorMessageIfRequestIdsForWrongEvent(wrongEventRequests, eventId);
            errorMessageBuilder.append(error);
        }
        if (!wrongStatusRequests.isEmpty()) {
            String error = getErrorMessageIfRequestIdsWithStatusNotPending(wrongStatusRequests);
            errorMessageBuilder.append(error);
        }

        errorMessageBuilder.delete(errorMessageBuilder.length() - 1, errorMessageBuilder.length());

        throw new ValidationException(errorMessageBuilder.toString(), LocalDateTime.now());
    }

    private String getErrorMessageIfRequestIdsForWrongEvent(List<Request> requests, Long eventId) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Expected Event ID=").append(eventId).append(" for the following requests. ");
        for (Request request : requests) {
            messageBuilder.append(String.format("request ID=%d (event ID=%d), ",
                    request.getId(), request.getEvent().getId()));
        }
        messageBuilder.delete(messageBuilder.length() - 2, messageBuilder.length());
        messageBuilder.append(". ");
        return messageBuilder.toString();
    }

    private String getErrorMessageIfRequestIdsWithStatusNotPending(List<Request> requests) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Expected status '").append(RequestStatus.PENDING)
                .append("' for the following requests: ");

        for (Request request : requests) {
            messageBuilder.append(String.format("request ID=%d (status: %s), ",
                    request.getId(), request.getStatus()));
        }
        messageBuilder.delete(messageBuilder.length() - 2, messageBuilder.length());
        messageBuilder.append(". ");

        return messageBuilder.toString();
    }
}
