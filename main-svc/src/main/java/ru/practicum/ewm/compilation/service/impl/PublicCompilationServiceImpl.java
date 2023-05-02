package ru.practicum.ewm.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.compilation.repository.EventCompilationRepository;
import ru.practicum.ewm.compilation.service.PublicCompilationService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compMapper;

    private final EventRepository eventRepository;

    private final EventCompilationRepository eventCompilationRepository;


    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = getCompilationWithoutEvents(compId);
        List<Long> eventIds = eventCompilationRepository.getEventIdsByCompId(compId);
        List<Event> events = eventRepository.findAllByIdIn(eventIds);
        compilation.setEvents(events);
        return compMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(compMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    // ---------------------------------------
    // Вспомогательные методы (обращение к БД)
    // ---------------------------------------

    private Compilation getCompilationWithoutEvents(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Compilation with id=%d was not found", compId),
                        Compilation.class,
                        LocalDateTime.now())
                );
    }
}
