package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", dateFormat = EWM_DATE_TIME_FORMAT)
    EndpointHit toEndpointHit(EndpointHitDto dto);

    @Mapping(target = "timestamp", source = "timestamp", dateFormat = EWM_DATE_TIME_FORMAT)
    EndpointHitDto toDto(EndpointHit hit);

}
