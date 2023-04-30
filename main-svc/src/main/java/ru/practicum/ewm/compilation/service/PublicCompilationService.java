package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PublicCompilationService {

    CompilationDto getById(Long compId);

    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

}
