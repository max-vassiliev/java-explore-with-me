package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.update.UpdateEventUserRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {

    EventFullDto create(Long initiatorId, NewEventDto newEvent);

    EventFullDto getByUserIdAndEventId(Long eventId, Long userId);

    List<EventShortDto> getAllByUserId(Long userId, Pageable pageable);

    List<ParticipationRequestDto> getRequestsByEventIdAndUserId(Long eventId, Long userId);

    EventFullDto update(Long eventId, Long userId, UpdateEventUserRequest updateDto);

    EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest inputDto,
                                                       Long eventId, Long userId);

}
