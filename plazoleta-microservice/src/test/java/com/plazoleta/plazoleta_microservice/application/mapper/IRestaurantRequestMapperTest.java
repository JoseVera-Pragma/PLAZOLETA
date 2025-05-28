package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class IRestaurantRequestMapperTest {
    private IRestaurantRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(IRestaurantRequestMapper.class);
    }

    @Test
    void toRestaurantShouldMapDtoToDomainCorrectly() {
        RestaurantRequestDto dto = RestaurantRequestDto.builder()
                .name("Restaurante Test")
                .nit("123456789")
                .address("Calle Falsa 123")
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(99L).build();

        Restaurant restaurant = mapper.toRestaurant(dto);

        assertNotNull(restaurant);
        assertEquals(dto.getName(), restaurant.getName());
        assertEquals(dto.getNit(), restaurant.getNit());
        assertEquals(dto.getAddress(), restaurant.getAddress());
        assertEquals(dto.getPhoneNumber(), restaurant.getPhoneNumber());
        assertEquals(dto.getUrlLogo(), restaurant.getUrlLogo());
        assertEquals(dto.getIdOwner(), restaurant.getIdOwner());
    }

    @Test
    void toRestaurantRequestDtoShouldMapDomainToDtoCorrectly() {
        // Arrange
        Restaurant restaurant = new Restaurant.Builder()
                .name("Mi Restaurante")
                .nit("654321")
                .address("Av Siempre Viva")
                .phoneNumber("+573009876543")
                .urlLogo("http://logo.com/logo2.png")
                .idOwner(42L)
                .build();

        // Act
        RestaurantRequestDto dto = mapper.toRestaurantRequestDto(restaurant);

        // Assert
        assertNotNull(dto);
        assertEquals(restaurant.getName(), dto.getName());
        assertEquals(restaurant.getNit(), dto.getNit());
        assertEquals(restaurant.getPhoneNumber(), dto.getPhoneNumber());

        // Nota: otros campos no están mapeados por el mapper y deben validarse si se agregan
    }

}