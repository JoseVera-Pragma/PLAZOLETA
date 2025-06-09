package com.plazoleta.traceability_microservice.domain.usecase;

import com.plazoleta.traceability_microservice.domain.api.ITraceabilityServicePort;
import com.plazoleta.traceability_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.traceability_microservice.domain.model.Traceability;
import com.plazoleta.traceability_microservice.domain.spi.IAuthenticatedUserPort;
import com.plazoleta.traceability_microservice.domain.spi.ITraceabilityPersistencePort;

import java.time.LocalDateTime;
import java.util.List;

public class TraceabilityUseCase implements ITraceabilityServicePort {

    private final ITraceabilityPersistencePort persistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public TraceabilityUseCase(ITraceabilityPersistencePort persistencePort, IAuthenticatedUserPort authenticatedUserPort) {
        this.persistencePort = persistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public void saveTraceability(Traceability traceability) {
        Traceability traceabilityToSave = traceability.whitDate(LocalDateTime.now());
        persistencePort.saveTraceability(traceabilityToSave);
    }

    @Override
    public List<Traceability> findTraceabilityForCustomer(Long orderId) {
        Long customerId = authenticatedUserPort.getCurrentUserId().orElseThrow( () ->
                new UserNotFoundException("User not found.")
        );
        return persistencePort.findTraceabilityByOrderAndCustomer(orderId, customerId);
    }
}
