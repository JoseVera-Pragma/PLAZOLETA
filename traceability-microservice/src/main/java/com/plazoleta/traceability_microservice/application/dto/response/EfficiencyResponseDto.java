package com.plazoleta.traceability_microservice.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EfficiencyResponseDto {
    private Long orderId;
    private Long durationInMinutes;
}
