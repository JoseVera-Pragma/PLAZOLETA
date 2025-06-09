package com.plazoleta.plazoleta_microservice.infrastructure.out.feign.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Traceability;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.client.TraceabilityFeignClient;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.dto.TraceabilityRequestDto;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.mapper.ITraceabilityRequestMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceabilityFeignAdapterTest {

    @Mock
    private TraceabilityFeignClient traceabilityFeignClient;

    @Mock
    private ITraceabilityRequestMapper traceabilityRequestMapper;

    @InjectMocks
    private TraceabilityFeignAdapter traceabilityFeignAdapter;

    @Test
    void saveTraceability_shouldCallFeignClientWithMappedRequest() {

        Traceability traceability = Traceability.builder()
                .orderId(1L)
                .customerId(10L)
                .customerEmail("customer@example.com")
                .newState("PENDING")
                .build();

        TraceabilityRequestDto request = TraceabilityRequestDto.builder()
                .orderId(1L)
                .customerId(10L)
                .customerEmail("customer@example.com")
                .newState("PENDING")
                .build();

        when(traceabilityRequestMapper.toTraceabilityRequest(traceability)).thenReturn(request);

        traceabilityFeignAdapter.saveTraceability(traceability);

        verify(traceabilityRequestMapper).toTraceabilityRequest(traceability);
        verify(traceabilityFeignClient).saveTraceability(request);
    }
}