package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.update.UpdateEventRequest;
import ru.practicum.ewm.event.model.Event;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EventMapperLite {

    @Mapping(target = "location", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = EWM_DATE_TIME_FORMAT)
    Event toEvent(NewEventDto dto);

    @Mapping(target = "location", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = EWM_DATE_TIME_FORMAT)
    @Mapping(target = "createdOn", source = "createdOn", dateFormat = EWM_DATE_TIME_FORMAT)
    @Mapping(target = "publishedOn", source = "publishedOn", dateFormat = EWM_DATE_TIME_FORMAT)
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = EWM_DATE_TIME_FORMAT)
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    void updateFields(UpdateEventRequest request, @MappingTarget Event event);
}
