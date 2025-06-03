package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishOrderRequestDto;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRequestMapperTest {

    private final OrderRequestMapper mapper = new OrderRequestMapper();
    @Test
    void toDomain_shouldMapDtoToOrderDomain() {
        CreateOrderRequestDto dto = new CreateOrderRequestDto();

        dto.setIdRestaurant(100L);
        dto.setDishes(new ArrayList<>(List.of(
                new DishOrderRequestDto(1L, 2),
                new DishOrderRequestDto(2L, 3)
        )));

        Order order = mapper.toDomain(dto);

        assertNull(order.getId());
        assertNull(order.getCustomerId());
        assertNull(order.getChefId());
        assertEquals(100L, order.getRestaurantId());
        assertNull(order.getOrderDate());
        assertNull(order.getStatus());

        assertNotNull(order.getDishes());
        assertEquals(2, order.getDishes().size());

        assertEquals(1L, order.getDishes().get(0).getDishId());
        assertEquals(2, order.getDishes().get(0).getQuantity());

        assertEquals(2L, order.getDishes().get(1).getDishId());
        assertEquals(3, order.getDishes().get(1).getQuantity());
    }
}