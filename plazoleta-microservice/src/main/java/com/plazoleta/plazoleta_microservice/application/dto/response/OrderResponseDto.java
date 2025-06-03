package com.plazoleta.plazoleta_microservice.application.dto.response;

import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private Long idCustomer;
    private Long idChef;
    private String orderDate;
    private OrderStatus status;
    private String statusDescription;
    private Long restaurantId;
}
