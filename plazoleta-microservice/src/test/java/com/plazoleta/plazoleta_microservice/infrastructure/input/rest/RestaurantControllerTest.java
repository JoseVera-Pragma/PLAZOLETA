package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRestaurantHandler restaurantHandler;

    @MockitoBean
    private IDishHandler dishHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createRestaurant_ShouldReturnCreated() throws Exception {
        RestaurantRequestDto requestDto = new RestaurantRequestDto();
        requestDto.setName("La Delicia");
        requestDto.setAddress("Calle 123 #45-67");
        requestDto.setPhoneNumber("+573001234567");
        requestDto.setUrlLogo("https://example.com/logo.jpg");
        requestDto.setNit("900123457");
        requestDto.setIdOwner(1L);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(restaurantHandler, times(1)).createRestaurant(any(RestaurantRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"OWNER"})
    void createDish_ShouldReturnCreatedDish() throws Exception {
        Long restaurantId = 1L;
        Long ownerId = 1L;

        DishRequestDto requestDto = new DishRequestDto();
        requestDto.setName("Bandeja Paisa");
        requestDto.setDescription("Plato típico colombiano");
        requestDto.setPrice(2.5);
        requestDto.setImageUrl("https://example.com/bandeja.jpg");
        requestDto.setCategoryName("test");

        DishResponseDto responseDto = new DishResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Bandeja Paisa");
        responseDto.setDescription("Plato típico colombiano");
        responseDto.setPrice(2.5);
        responseDto.setImageUrl("https://example.com/bandeja.jpg");
        responseDto.setActive(true);
        responseDto.setRestaurantId(restaurantId);
        responseDto.setCategoryName("test");

        when(dishHandler.createDish(anyLong(), any(DishRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/restaurants/{restaurantId}/dishes", restaurantId)
                        .param("ownerId", ownerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bandeja Paisa"))
                .andExpect(jsonPath("$.description").value("Plato típico colombiano"))
                .andExpect(jsonPath("$.price").value(2.5))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/bandeja.jpg"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.categoryName").value("test"));

        verify(dishHandler, times(1)).createDish(eq(restaurantId), any(DishRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createRestaurant_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        RestaurantRequestDto invalidRequest = new RestaurantRequestDto();

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createDish_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Long restaurantId = 1L;
        Long ownerId = 1L;

        DishRequestDto invalidRequest = new DishRequestDto();

        mockMvc.perform(post("/restaurants/{restaurantId}/dishes", restaurantId)
                        .param("ownerId", ownerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createDish_WithoutOwnerIdParam_ShouldReturnBadRequest() throws Exception {
        Long restaurantId = 1L;

        DishRequestDto requestDto = new DishRequestDto();
        requestDto.setName("Test Dish");
        requestDto.setDescription("Test Description");
        requestDto.setPrice(1.0);
        requestDto.setCategoryName("test");

        mockMvc.perform(post("/restaurants/{restaurantId}/dishes", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"OWNER"})
    void updateDish_ReturnsNoContent_WhenSuccessful() throws Exception {
        Long restaurantId = 1L;
        Long dishId = 10L;
        Long ownerId = 100L;

        String jsonBody = """
            {
              "description": "Updated description",
              "price": 15.5
            }
            """;

        mockMvc.perform(put("/restaurants/{restaurantId}/dishes/{dishId}", restaurantId, dishId)
                        .param("ownerId", ownerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isNoContent());

        verify(dishHandler).updateDish(eq(dishId), any(DishUpdateRequestDto.class));
    }


    @Test
    @WithMockUser(roles = "OWNER")
    void shouldChangeDishStatusSuccessfully() throws Exception {
        Long dishId = 1L;
        boolean activate = true;

        mockMvc.perform(patch("/restaurants/dishes/"+dishId+"/status")
                        .param("activate", String.valueOf(activate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(dishHandler).changeDishStatus(dishId, activate);
    }
}