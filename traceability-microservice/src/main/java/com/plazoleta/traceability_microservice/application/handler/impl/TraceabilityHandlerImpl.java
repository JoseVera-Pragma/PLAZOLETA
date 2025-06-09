package com.plazoleta.traceability_microservice.application.handler.impl;

import com.plazoleta.traceability_microservice.application.dto.request.TraceabilityRequestDto;
import com.plazoleta.traceability_microservice.application.dto.response.TraceabilityResponseDto;
import com.plazoleta.traceability_microservice.application.handler.ITraceabilityHandler;
import com.plazoleta.traceability_microservice.application.mapper.TraceabilityRequestMapper;
import com.plazoleta.traceability_microservice.application.mapper.TraceabilityResponseMapper;
import com.plazoleta.traceability_microservice.domain.api.ITraceabilityServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
public class TraceabilityHandlerImpl implements ITraceabilityHandler {

    private final ITraceabilityServicePort servicePort;
    private final TraceabilityRequestMapper traceabilityRequestMapper;
    private final TraceabilityResponseMapper traceabilityResponseMapper;

    @Override
    public void saveTraceability(TraceabilityRequestDto requestDTO) {
        servicePort.saveTraceability(traceabilityRequestMapper.toTraceability(requestDTO));
    }

    @Override
    public List<TraceabilityResponseDto> getTraceabilityByOrder(Long orderId) {
        return servicePort.findTraceabilityForCustomer(orderId).stream()
                .map(traceabilityResponseMapper::toTraceabilityResponseDto)
                .toList();
    }
}
