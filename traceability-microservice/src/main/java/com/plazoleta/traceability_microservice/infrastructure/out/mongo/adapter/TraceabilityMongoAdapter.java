package com.plazoleta.traceability_microservice.infrastructure.out.mongo.adapter;

import com.plazoleta.traceability_microservice.domain.model.Traceability;
import com.plazoleta.traceability_microservice.domain.spi.ITraceabilityPersistencePort;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.mapper.TraceabilityDocumentMapper;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.repository.TraceabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TraceabilityMongoAdapter implements ITraceabilityPersistencePort {

    private final TraceabilityRepository repository;
    private final TraceabilityDocumentMapper mapper;

    @Override
    public void saveTraceability(Traceability traceability) {
        repository.save(mapper.toTraceabilityDocument(traceability));
    }

    @Override
    public List<Traceability> findTraceabilityByOrderAndCustomer(Long orderId, Long customerId) {
        return repository.findByOrderIdAndCustomerId(orderId, customerId)
                .stream()
                .map(mapper::toTraceability)
                .toList();
    }
}
