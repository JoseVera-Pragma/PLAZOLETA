package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IOrderResponseMapperTest {

    private final IOrderResponseMapper mapper = Mappers.getMapper(IOrderResponseMapper.class);

    @Test
    void shouldMapOrderToResponseDtoWithSpanishStatus() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.IN_PREPARATION)
                .build();

        OrderResponseDto dto = mapper.toResponseDto(order);

        assertEquals("En preparación", dto.getStatusDescription());
    }

    @Test
    void shouldMapListOfOrders() {
        Order order1 = Order.builder().id(1L).status(OrderStatus.READY).build();
        Order order2 = Order.builder().id(2L).status(OrderStatus.DELIVERED).build();
        Order order3 = Order.builder().id(3L).status(OrderStatus.PENDING).build();
        Order order4 = Order.builder().id(4L).status(OrderStatus.CANCELLED).build();
        List<Order> orders = List.of(order1, order2, order3, order4);

        List<OrderResponseDto> responseDtos = mapper.toResponsesDto(orders);

        assertEquals(4, responseDtos.size());
        assertEquals("Listo", responseDtos.get(0).getStatusDescription());
        assertEquals("Entregado", responseDtos.get(1).getStatusDescription());
    }
}
