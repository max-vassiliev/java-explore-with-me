package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.params.EventPublicSearchParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventService {

    EventFullDto getById(Long id, HttpServletRequest request);

    List<EventShortDto> getAll(EventPublicSearchParams params, HttpServletRequest request);

}
