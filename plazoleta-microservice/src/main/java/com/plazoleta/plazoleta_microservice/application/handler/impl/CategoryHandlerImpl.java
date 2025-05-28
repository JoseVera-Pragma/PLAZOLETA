package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.CategoryRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.CategoryResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.ICategoryHandler;
import com.plazoleta.plazoleta_microservice.application.mapper.ICategoryRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.ICategoryResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryHandlerImpl implements ICategoryHandler {
    private final ICategoryServicePort categoryServicePort;
    private final ICategoryRequestMapper categoryRequestMapper;
    private final ICategoryResponseMapper categoryResponseMapper;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        return categoryResponseMapper.toCategoryResponse(categoryServicePort.saveCategory(categoryRequestMapper.toCategory(categoryRequestDto)));
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        return categoryResponseMapper.toCategoryResponse(categoryServicePort.getCategoryById(id));
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryResponseMapper.toCategoryResponseList(categoryServicePort.getAllCategories());
    }

    @Override
    public CategoryResponseDto updateCategory(Long id ,CategoryRequestDto categoryRequestDto) {
        return categoryResponseMapper.toCategoryResponse(categoryServicePort.updateCategory(id, categoryRequestMapper.toCategory(categoryRequestDto)));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryServicePort.deleteCategoryById(id);
    }
}
