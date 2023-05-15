package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.PublicCompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class PublicCompilationController {

    private final PublicCompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        log.info("GET /compilations/{}", compId);
        return compilationService.getById(compId);
    }

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(name = "pinned", required = false) Boolean pinned,
                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET /compilations?pinned={}&from={}&size={}", pinned, from, size);
        return compilationService.getAll(pinned, new CustomPageRequest(from, size, Sort.by("id")));
    }
}
