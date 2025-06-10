package com.plazoleta.traceability_microservice.application.handler.impl;

import com.plazoleta.traceability_microservice.application.dto.request.TraceabilityRequestDto;
import com.plazoleta.traceability_microservice.application.dto.response.EfficiencyResponseDto;
import com.plazoleta.traceability_microservice.application.dto.response.EmployeeEfficiencyRankingResponseDto;
import com.plazoleta.traceability_microservice.application.dto.response.TraceabilityResponseDto;
import com.plazoleta.traceability_microservice.application.handler.ITraceabilityHandler;
import com.plazoleta.traceability_microservice.application.mapper.IEfficiencyResponseMapper;
import com.plazoleta.traceability_microservice.application.mapper.IEmployeeEfficiencyRankingResponseMapper;
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
    private final IEfficiencyResponseMapper efficiencyResponseMapper;
    private final IEmployeeEfficiencyRankingResponseMapper employeeEfficiencyRankingResponseMapper;

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

    @Override
    public List<EfficiencyResponseDto> getOrderEfficiencies(Long restaurantId) {
        return servicePort.getOrderEfficienciesByRestaurant(restaurantId)
                .stream()
                .map(efficiencyResponseMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<EmployeeEfficiencyRankingResponseDto> getEmployeeRankingByRestaurant(Long restaurantId) {
        return servicePort.getEmployeeEfficiencyRanking(restaurantId)
                .stream()
                .map(employeeEfficiencyRankingResponseMapper::toDto)
                .toList();
    }
}
