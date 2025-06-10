package com.plazoleta.traceability_microservice.domain.spi;

import com.plazoleta.traceability_microservice.domain.model.Traceability;

import java.util.List;

public interface ITraceabilityPersistencePort {
    void saveTraceability(Traceability traceability);
    List<Traceability> findTraceabilityByOrderAndCustomer(Long orderId, Long customerId);
    List<Traceability> findAllByRestaurantId(Long restaurantId);
}
