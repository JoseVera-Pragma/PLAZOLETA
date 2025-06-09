package com.plazoleta.plazoleta_microservice.infrastructure.out.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TraceabilityRequestDto {
    private Long orderId;
    private Long customerId;
    private String customerEmail;
    private String previousState;
    private String newState;
    private Long employedId;
    private String employedEmail;
}