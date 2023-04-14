package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStatsDto toDto(ViewStats stats);

}
