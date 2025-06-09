package com.plazoleta.traceability_microservice.domain.api;

import com.plazoleta.traceability_microservice.domain.model.Traceability;

import java.util.List;

public interface ITraceabilityServicePort {
    void saveTraceability(Traceability traceability);
    List<Traceability> findTraceabilityForCustomer(Long orderId);
}
