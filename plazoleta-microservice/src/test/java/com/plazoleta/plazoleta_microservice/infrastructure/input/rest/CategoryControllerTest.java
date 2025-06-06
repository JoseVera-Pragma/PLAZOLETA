package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.CategoryRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.CategoryResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.ICategoryHandler;
import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICategoryHandler categoryHandler;

    @MockitoBean
    private JwtTokenAdapter jwtTokenAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryRequestDto categoryRequestDto;
    private CategoryResponseDto categoryResponseDto;

    @BeforeEach
    void setUp() {
        categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Entradas");
        categoryRequestDto.setDescription("Description");

        categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(1L);
        categoryResponseDto.setName("Entradas");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createCategory_shouldReturnCreated() throws Exception {
        when(categoryHandler.createCategory(any(CategoryRequestDto.class)))
                .thenReturn(categoryResponseDto);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(categoryResponseDto.getName()));
    }

    @Test
    @WithMockUser(roles = {"OWNER"})
    void getAllCategories_shouldReturnList() throws Exception {
        when(categoryHandler.getAllCategories())
                .thenReturn(List.of(categoryResponseDto));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(categoryResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(categoryResponseDto.getName()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteCategoryById_shouldReturnNoContent() throws Exception {
        doNothing().when(categoryHandler).deleteCategoryById(1L);

        mockMvc.perform(delete("/categories/{categoryId}", 1L))
                .andExpect(status().isNoContent());
    }
}
