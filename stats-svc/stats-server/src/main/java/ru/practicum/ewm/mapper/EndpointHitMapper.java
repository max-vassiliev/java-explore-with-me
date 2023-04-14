package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", dateFormat="yyyy-MM-dd HH:mm:ss")
    EndpointHit toEndpointHit(EndpointHitDto dto);

    @Mapping(target = "timestamp", source = "timestamp", dateFormat="yyyy-MM-dd HH:mm:ss")
    EndpointHitDto toDto(EndpointHit hit);

}
