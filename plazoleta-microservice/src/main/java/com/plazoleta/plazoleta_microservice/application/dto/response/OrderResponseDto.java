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
    private Long customerId;
    private Long chefId;
    private String orderDate;
    private OrderStatus status;
    private String statusDescription;
    private String securityPin;
    private Long restaurantId;
}
