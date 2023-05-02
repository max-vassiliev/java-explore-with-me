package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.model.EntityNotFoundException;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto categoryDto) {
        Category category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto getById(Long catId) {
        return categoryMapper.toCategoryDto(getCategory(catId));
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        List<Category> categories = categoryRepository.findAll(pageable).toList();

        if (categories.isEmpty()) return Collections.emptyList();
        return categories.stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, NewCategoryDto categoryDto) {
        Category category = getCategory(catId);
        category.setName(categoryDto.getName());
        categoryRepository.flush();
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        getCategory(catId);
        categoryRepository.deleteById(catId);
        categoryRepository.flush();
    }

    // -------------------------
    // Вспомогательные методы
    // -------------------------

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Category with id=%d was not found", id),
                        Category.class,
                        LocalDateTime.now())
                );
    }
}
