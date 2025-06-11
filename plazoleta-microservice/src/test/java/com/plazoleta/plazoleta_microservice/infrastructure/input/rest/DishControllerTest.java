package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DishController.class)
@AutoConfigureMockMvc(addFilters = false)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDishHandler dishHandler;

    @MockitoBean
    private JwtTokenAdapter jwtTokenAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    private DishRequestDto dishRequestDto;
    private DishResponseDto dishResponseDto;
    private DishUpdateRequestDto dishUpdateRequestDto;

    @BeforeEach
    void setUp() {
        dishRequestDto = DishRequestDto.builder()
                .name("Plato Test")
                .description("Descripción de prueba")
                .price(1500.0)
                .imageUrl("http://imagen.com/plato.jpg")
                .restaurantId(1L)
                .categoryName("test")
                .build();

        dishResponseDto = DishResponseDto.builder()
                .id(1L)
                .name("Plato Test")
                .description("Descripción de prueba")
                .price(1500.0)
                .imageUrl("http://imagen.com/plato.jpg")
                .restaurantId(1L)
                .categoryName("test")
                .active(true)
                .build();

        dishUpdateRequestDto = new DishUpdateRequestDto("Nueva descripción",1600.0);
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void createDish_shouldReturnCreated() throws Exception {
        when(dishHandler.createDish(any(DishRequestDto.class))).thenReturn(dishResponseDto);

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Plato Test"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void updateDishPriceAndDescription_shouldReturnNoContent() throws Exception {
        doNothing().when(dishHandler).updateDishPriceAndDescription(1L, dishUpdateRequestDto);

        mockMvc.perform(patch("/dishes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishUpdateRequestDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void changeDishStatus_shouldReturnNoContent() throws Exception {
        doNothing().when(dishHandler).changeDishStatus(1L, true);

        mockMvc.perform(patch("/dishes/1/status")
                        .param("activate", "true"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getDishesByCategory_shouldReturnPage() throws Exception {
        DishResponseDto dishResponseDto = DishResponseDto.builder()
                .id(1L)
                .name("Plato Test")
                .active(true)
                .build();

        List<DishResponseDto> content = List.of(dishResponseDto);

        Page<DishResponseDto> pagedResponse = new Page<>(
                content,
                0,
                10,
                1L
        );

        when(dishHandler.getDishesByRestaurantAndCategory(1L, 2L, 0, 10)).thenReturn(pagedResponse);

        mockMvc.perform(get("/dishes/restaurant/1/category/2")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Plato Test"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}