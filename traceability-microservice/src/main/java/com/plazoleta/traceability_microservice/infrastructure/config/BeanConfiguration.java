package com.plazoleta.traceability_microservice.infrastructure.config;

import com.plazoleta.traceability_microservice.application.handler.ITraceabilityHandler;
import com.plazoleta.traceability_microservice.application.handler.impl.TraceabilityHandlerImpl;
import com.plazoleta.traceability_microservice.application.mapper.IEfficiencyResponseMapper;
import com.plazoleta.traceability_microservice.application.mapper.IEmployeeEfficiencyRankingResponseMapper;
import com.plazoleta.traceability_microservice.application.mapper.TraceabilityRequestMapper;
import com.plazoleta.traceability_microservice.application.mapper.TraceabilityResponseMapper;
import com.plazoleta.traceability_microservice.domain.api.ITraceabilityServicePort;
import com.plazoleta.traceability_microservice.domain.spi.IAuthenticatedUserPort;
import com.plazoleta.traceability_microservice.domain.spi.ITraceabilityPersistencePort;
import com.plazoleta.traceability_microservice.domain.usecase.TraceabilityUseCase;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.adapter.TraceabilityMongoAdapter;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.mapper.TraceabilityDocumentMapper;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.repository.TraceabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final TraceabilityRepository traceabilityRepository;
    private final TraceabilityRequestMapper traceabilityRequestMapper;
    private final TraceabilityResponseMapper traceabilityResponseMapper;
    private final TraceabilityDocumentMapper traceabilityDocumentMapper;
    private final IEfficiencyResponseMapper efficiencyResponseMapper;
    private final IEmployeeEfficiencyRankingResponseMapper employeeEfficiencyRankingResponseMapper;

    @Bean
    public ITraceabilityPersistencePort traceabilityPersistencePort() {
        return new TraceabilityMongoAdapter(traceabilityRepository, traceabilityDocumentMapper);
    }

    @Bean
    public ITraceabilityServicePort traceabilityServicePort(ITraceabilityPersistencePort traceabilityPersistencePort, IAuthenticatedUserPort authenticatedUserPort) {
        return new TraceabilityUseCase(traceabilityPersistencePort, authenticatedUserPort);
    }

    @Bean
    public ITraceabilityHandler traceabilityHandler(ITraceabilityServicePort traceabilityServicePort) {
        return new TraceabilityHandlerImpl(traceabilityServicePort, traceabilityRequestMapper, traceabilityResponseMapper, efficiencyResponseMapper, employeeEfficiencyRankingResponseMapper);
    }

}