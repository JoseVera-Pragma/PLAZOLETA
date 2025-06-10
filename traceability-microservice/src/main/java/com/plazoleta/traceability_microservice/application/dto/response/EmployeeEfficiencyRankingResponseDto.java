package com.plazoleta.traceability_microservice.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEfficiencyRankingResponseDto {
    private Long employeeId;
    private String employeeEmail;
    private Long durationInMinutes;
}
