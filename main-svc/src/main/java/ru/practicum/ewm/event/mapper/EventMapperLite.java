package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.update.UpdateEventRequest;
import ru.practicum.ewm.event.model.Event;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EventMapperLite {

    String EWM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
    @Mapping(target = "rating", qualifiedByName = "roundToOneDecimal")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = EWM_DATE_TIME_FORMAT)
    @Mapping(target = "rating", qualifiedByName = "roundToOneDecimal")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    void updateFields(UpdateEventRequest request, @MappingTarget Event event);

    @Named("roundToOneDecimal")
    default Float roundRating(Float rating) {
        if (rating == null) {
            return null;
        }
        return Math.round(rating * 10.0f) / 10.0f;
    }
}
