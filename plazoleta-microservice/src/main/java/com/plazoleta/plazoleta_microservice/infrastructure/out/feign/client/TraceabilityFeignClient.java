package com.plazoleta.plazoleta_microservice.infrastructure.out.feign.client;

import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.FeignConfig;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.dto.TraceabilityRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "traceability-microservice", url = "${traceability.service.url}",
        configuration = FeignConfig.class)
public interface TraceabilityFeignClient {
    @PostMapping("/traceability")
    void saveTraceability (@RequestBody TraceabilityRequestDto traceabilityRequestDto);
}
