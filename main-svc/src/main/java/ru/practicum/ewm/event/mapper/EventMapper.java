package ru.practicum.ewm.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.params.CreateEventParams;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.mapper.UserMapper;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventMapper {

    private final EventMapperLite eventMapperLite;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;
    private final CategoryMapper categoryMapper;


    public Event toNewEvent(NewEventDto newEventDto, CreateEventParams params) {
        Event event = eventMapperLite.toEvent(newEventDto);
        addFieldsBeforeSave(event, params);
        return event;
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto dto = eventMapperLite.toEventFullDto(event);
        addFieldsToEventFullDto(dto, event);
        return dto;
    }

    public EventShortDto toEventShortDto(Event event) {
        EventShortDto dto = eventMapperLite.toEventShortDto(event);
        addFieldsToEventShortDto(dto, event);
        return dto;
    }


    // ------------------------
    // Вспомогательные методы
    // ------------------------

    private void addFieldsToEventFullDto(EventFullDto dto, Event event) {
        dto.setLocation(locationMapper.toLocationDto(event.getLocation()));
        dto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        dto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
    }

    private void addFieldsToEventShortDto(EventShortDto dto, Event event) {
        dto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        dto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
    }

    private void addFieldsBeforeSave(Event event, CreateEventParams params) {
        event.setCreatedOn(params.getCreatedOn());
        event.setEventDate(params.getEventDate());
        event.setInitiator(params.getInitiator());
        event.setCategory(params.getCategory());
        event.setLocation(params.getLocation());
        event.setState(EventState.PENDING);
        event.setViews(0L);
    }
}
