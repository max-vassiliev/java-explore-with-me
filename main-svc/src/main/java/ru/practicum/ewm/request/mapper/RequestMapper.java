package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    String EWM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "created", source = "created", dateFormat = EWM_DATE_TIME_FORMAT)
    ParticipationRequestDto toParticipationRequestDto(Request request);

}
