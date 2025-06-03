package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.Order;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IOrderResponseMapperImpl.class})
class IOrderResponseMapperTest {

    @Autowired
    private IOrderResponseMapper orderResponseMapper;

    @Test
    void toResponseDto_ShouldMapOrderToDtoWithSpanishStatus() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.IN_PREPARATION);

        OrderResponseDto dto = orderResponseMapper.toResponseDto(order);

        assertNotNull(dto);
        assertEquals("En preparación", dto.getStatusDescription());
    }

    @Test
    void toResponsesDto_ShouldMapList() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.PENDING);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(OrderStatus.READY);

        Order order3 = new Order();
        order3.setId(2L);
        order3.setStatus(OrderStatus.IN_PREPARATION);

        Order order4 = new Order();
        order4.setId(2L);
        order4.setStatus(OrderStatus.CANCELLED);

        Order order5 = new Order();
        order5.setId(2L);
        order5.setStatus(OrderStatus.DELIVERED);

        List<Order> orders = List.of(order1, order2, order3, order4, order5);

        List<OrderResponseDto> dtos = orderResponseMapper.toResponsesDto(orders);

        assertEquals(5, dtos.size());
        assertEquals("Pendiente", dtos.get(0).getStatusDescription());
        assertEquals("Listo", dtos.get(1).getStatusDescription());
        assertEquals("En preparación", dtos.get(2).getStatusDescription());
        assertEquals("Cancelado", dtos.get(3).getStatusDescription());
        assertEquals("Entregado", dtos.get(4).getStatusDescription());
    }

}