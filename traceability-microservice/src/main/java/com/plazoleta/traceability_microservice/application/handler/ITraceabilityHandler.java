package com.plazoleta.traceability_microservice.application.handler;

import com.plazoleta.traceability_microservice.application.dto.request.TraceabilityRequestDto;
import com.plazoleta.traceability_microservice.application.dto.response.TraceabilityResponseDto;

import java.util.List;

public interface ITraceabilityHandler {
    void saveTraceability(TraceabilityRequestDto requestDTO);
    List<TraceabilityResponseDto> getTraceabilityByOrder(Long orderId);
}
