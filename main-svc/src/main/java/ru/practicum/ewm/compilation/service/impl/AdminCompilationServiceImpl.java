package ru.practicum.ewm.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.mapper.CompilationMapperLite;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.compilation.service.AdminCompilationService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapperLite compMapperLite;

    private final CompilationMapper compMapper;

    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        List<Event> events = getEvents(dto);
        Compilation compilation = compMapperLite.toCompilation(dto);
        compilation.setEvents(events);
        return compMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = getCompilation(compId);
        Compilation updatedCompilation = updateFields(compilation, updateRequest);
        compilationRepository.flush();
        return compMapper.toCompilationDto(updatedCompilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        getCompilation(compId);
        compilationRepository.deleteById(compId);
        compilationRepository.flush();
    }

    // ------------------------------------------
    // Вспомогательные методы (обращение к БД)
    // ------------------------------------------

    private List<Event> getEvents(NewCompilationDto dto) {
        if (dto.getEvents().isEmpty()) return Collections.emptyList();
        List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
        checkMissingEvents(events, dto.getEvents());
        return events;
    }

    private List<Event> getEvents(UpdateCompilationRequest request) {
        if (request.getEvents().isEmpty()) return Collections.emptyList();
        List<Event> events = eventRepository.findAllByIdIn(request.getEvents());
        checkMissingEvents(events, request.getEvents());
        return events;
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Compilation with id=%d was not found", compId),
                        Compilation.class,
                        LocalDateTime.now())
                );
    }

    // -------------------------
    // Вспомогательные методы
    // -------------------------

    private Compilation updateFields(Compilation compilation, UpdateCompilationRequest request) {
        if (request.getEvents() != null) {
            compilation.setEvents(getEvents(request));
        }
        compMapperLite.updateFields(request, compilation);
        return compilation;
    }

    // ------------
    // Валидация
    // ------------

    private void checkMissingEvents(List<Event> events, List<Long> inputEventIds) {
        if (events.size() == inputEventIds.size()) return;
        collectErrorsOnCompilationCreate(events, inputEventIds);
    }

    private void collectErrorsOnCompilationCreate(List<Event> events, List<Long> inputEventIds) {
        List<Long> foundEventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Long> notFoundEventIds = inputEventIds.stream()
                .filter(id -> !foundEventIds.contains(id))
                .collect(Collectors.toList());

        throw new EntityNotFoundException(
                "Events not found for the following IDs: " + notFoundEventIds,
                Event.class,
                LocalDateTime.now()
        );
    }
}
