package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapperLite {

    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto dto);

    @Mapping(target = "events", ignore = true)
    CompilationDto toCompilationDto(Compilation compilation);

}
