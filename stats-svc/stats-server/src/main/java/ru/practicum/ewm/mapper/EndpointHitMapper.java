package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    String EWM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", dateFormat = EWM_DATE_TIME_FORMAT)
    EndpointHit toEndpointHit(EndpointHitDto dto);

    @Mapping(target = "timestamp", source = "timestamp", dateFormat = EWM_DATE_TIME_FORMAT)
    EndpointHitDto toDto(EndpointHit hit);

}
