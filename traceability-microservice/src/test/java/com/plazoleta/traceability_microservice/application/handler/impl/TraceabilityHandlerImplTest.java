package com.plazoleta.traceability_microservice.application.handler.impl;

import com.plazoleta.traceability_microservice.application.dto.request.TraceabilityRequestDto;
import com.plazoleta.traceability_microservice.application.dto.response.TraceabilityResponseDto;
import com.plazoleta.traceability_microservice.application.mapper.TraceabilityRequestMapper;
import com.plazoleta.traceability_microservice.application.mapper.TraceabilityResponseMapper;
import com.plazoleta.traceability_microservice.domain.api.ITraceabilityServicePort;
import com.plazoleta.traceability_microservice.domain.model.Traceability;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceabilityHandlerImplTest {

    @Mock
    private ITraceabilityServicePort servicePort;
    @Mock
    private TraceabilityRequestMapper requestMapper;
    @Mock
    private TraceabilityResponseMapper responseMapper;

    @InjectMocks
    private TraceabilityHandlerImpl handler;

    @Test
    void saveTraceability_shouldCallServiceWithMappedModel() {
        TraceabilityRequestDto dto = new TraceabilityRequestDto();
        Traceability traceability = Traceability.builder()
                .orderId(1L)
                .customerId(2L)
                .customerEmail("customer@mail.com")
                .previousState("PENDING")
                .newState("READY")
                .build();

        when(requestMapper.toTraceability(dto)).thenReturn(traceability);

        handler.saveTraceability(dto);

        verify(requestMapper).toTraceability(dto);
        verify(servicePort).saveTraceability(traceability);
    }

    @Test
    void getTraceabilityByOrder_shouldReturnMappedResponseDto() {
        Long orderId = 1L;
        Traceability traceability = Traceability.builder()
                .orderId(orderId)
                .customerId(2L)
                .customerEmail("customer@mail.com")
                .date(LocalDateTime.now())
                .previousState("PENDING")
                .newState("READY")
                .build();

        TraceabilityResponseDto dto = new TraceabilityResponseDto();

        when(servicePort.findTraceabilityForCustomer(orderId)).thenReturn(List.of(traceability));
        when(responseMapper.toTraceabilityResponseDto(traceability)).thenReturn(dto);

        List<TraceabilityResponseDto> result = handler.getTraceabilityByOrder(orderId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());
        verify(servicePort).findTraceabilityForCustomer(orderId);
        verify(responseMapper).toTraceabilityResponseDto(traceability);
    }
}