package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.application.dto.request.CategoryRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.CategoryResponseDto;
import com.plazoleta.plazoleta_microservice.application.mapper.ICategoryRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.ICategoryResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryHandlerImplTest {

    @Mock
    private ICategoryServicePort categoryServicePort;

    @Mock
    private ICategoryRequestMapper categoryRequestMapper;

    @Mock
    private ICategoryResponseMapper categoryResponseMapper;

    @InjectMocks
    private CategoryHandlerImpl categoryHandler;

    @Test
    void createCategory_shouldReturnCategoryResponseDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        Category category = new Category(1L, "Name", "Description");
        CategoryResponseDto responseDto = new CategoryResponseDto();

        when(categoryRequestMapper.toCategory(requestDto)).thenReturn(category);
        when(categoryServicePort.saveCategory(category)).thenReturn(category);
        when(categoryResponseMapper.toCategoryResponse(category)).thenReturn(responseDto);

        CategoryResponseDto result = categoryHandler.createCategory(requestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(categoryRequestMapper).toCategory(requestDto);
        verify(categoryServicePort).saveCategory(category);
        verify(categoryResponseMapper).toCategoryResponse(category);
    }

    @Test
    void getCategoryById_shouldReturnCategoryResponseDto() {
        Long id = 1L;
        Category category = new Category(1L, "Name", "Description");
        CategoryResponseDto responseDto = new CategoryResponseDto();

        when(categoryServicePort.getCategoryById(id)).thenReturn(category);
        when(categoryResponseMapper.toCategoryResponse(category)).thenReturn(responseDto);

        CategoryResponseDto result = categoryHandler.getCategoryById(id);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(categoryServicePort).getCategoryById(id);
        verify(categoryResponseMapper).toCategoryResponse(category);
    }

    @Test
    void getAllCategories_shouldReturnListOfCategoryResponseDto() {
        List<Category> categories = List.of(new Category(1L, "Name", "Description")
                , new Category(1L, "Name", "Description"));
        List<CategoryResponseDto> responseDtos = List.of(new CategoryResponseDto(), new CategoryResponseDto());

        when(categoryServicePort.getAllCategories()).thenReturn(categories);
        when(categoryResponseMapper.toCategoryResponseList(categories)).thenReturn(responseDtos);

        List<CategoryResponseDto> result = categoryHandler.getAllCategories();

        assertNotNull(result);
        assertEquals(responseDtos.size(), result.size());
        assertEquals(responseDtos, result);

        verify(categoryServicePort).getAllCategories();
        verify(categoryResponseMapper).toCategoryResponseList(categories);
    }

    @Test
    void updateCategory_shouldReturnCategoryResponseDto() {
        Long id = 1L;
        CategoryRequestDto requestDto = new CategoryRequestDto();
        Category category = new Category(1L, "Name", "Description");
        CategoryResponseDto responseDto = new CategoryResponseDto();

        when(categoryRequestMapper.toCategory(requestDto)).thenReturn(category);
        when(categoryServicePort.updateCategory(id, category)).thenReturn(category);
        when(categoryResponseMapper.toCategoryResponse(category)).thenReturn(responseDto);

        CategoryResponseDto result = categoryHandler.updateCategory(id, requestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(categoryRequestMapper).toCategory(requestDto);
        verify(categoryServicePort).updateCategory(id, category);
        verify(categoryResponseMapper).toCategoryResponse(category);
    }

    @Test
    void deleteCategoryById_shouldCallServiceDelete() {
        Long id = 1L;

        doNothing().when(categoryServicePort).deleteCategoryById(id);

        categoryHandler.deleteCategoryById(id);

        verify(categoryServicePort).deleteCategoryById(id);
    }
}