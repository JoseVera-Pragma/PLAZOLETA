package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.CategoryRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.CategoryResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.ICategoryHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICategoryHandler categoryHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Electronics");
        requestDto.setDescription("Electronic devices and gadgets");

        CategoryResponseDto responseDto = new CategoryResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Electronics");
        responseDto.setDescription("Electronic devices and gadgets");

        when(categoryHandler.createCategory(any(CategoryRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic devices and gadgets"));

        verify(categoryHandler, times(1)).createCategory(any(CategoryRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        CategoryResponseDto category1 = new CategoryResponseDto();
        category1.setId(1L);
        category1.setName("Electronics");
        category1.setDescription("Electronic devices");

        CategoryResponseDto category2 = new CategoryResponseDto();
        category2.setId(2L);
        category2.setName("Books");
        category2.setDescription("Books and literature");

        List<CategoryResponseDto> categories = Arrays.asList(category1, category2);

        when(categoryHandler.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Books"));

        verify(categoryHandler, times(1)).getAllCategories();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteCategoryById_ShouldReturnNoContent() throws Exception {
        Long categoryId = 1L;
        doNothing().when(categoryHandler).deleteCategoryById(categoryId);

        mockMvc.perform(delete("/categories/{categoryId}", categoryId))
                .andExpect(status().isNoContent());

        verify(categoryHandler, times(1)).deleteCategoryById(categoryId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CategoryRequestDto invalidRequest = new CategoryRequestDto();

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}