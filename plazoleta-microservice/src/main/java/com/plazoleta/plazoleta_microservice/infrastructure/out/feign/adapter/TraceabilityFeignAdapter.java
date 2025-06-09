package com.plazoleta.plazoleta_microservice.infrastructure.out.feign.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Traceability;
import com.plazoleta.plazoleta_microservice.domain.spi.ITraceabilityClientPort;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.client.TraceabilityFeignClient;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.mapper.ITraceabilityRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraceabilityFeignAdapter implements ITraceabilityClientPort {

    private final TraceabilityFeignClient traceabilityFeignClient;
    private final ITraceabilityRequestMapper traceabilityRequestMapper;

    @Override
    public void saveTraceability(Traceability traceability) {
        traceabilityFeignClient.saveTraceability(traceabilityRequestMapper.toTraceabilityRequest(traceability));
    }
}
