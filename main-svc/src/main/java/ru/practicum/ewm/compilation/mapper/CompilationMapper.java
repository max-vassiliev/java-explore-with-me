package ru.practicum.ewm.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationMapper {

    private final CompilationMapperLite compMapperLite;

    private final EventMapper eventMapper;


    public CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto dto = compMapperLite.toCompilationDto(compilation);
        List<EventShortDto> eventDtos = compilation.getEvents().stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
        dto.setEvents(eventDtos);
        return dto;
    }
}
