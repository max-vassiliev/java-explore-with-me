package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.update.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.params.EventAdminSearchParams;

import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getAll(EventAdminSearchParams params);

    EventFullDto update(Long eventId, UpdateEventAdminRequest updateDto);

}
