package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto categoryDto);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto update(Long catId, NewCategoryDto categoryDto);

    void delete(Long catId);

}
