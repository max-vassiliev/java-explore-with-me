package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.common.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@Slf4j
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long catId) {
        log.info("GET /categories/{}", catId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getById(catId));
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET /categories?from={}&size={}", from, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getAll(new CustomPageRequest(from, size, Sort.by("id"))));
    }
}
