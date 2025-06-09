package com.plazoleta.traceability_microservice.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TraceabilityResponseDto {
    private String previousState;
    private String newState;
    private LocalDateTime date;
}
