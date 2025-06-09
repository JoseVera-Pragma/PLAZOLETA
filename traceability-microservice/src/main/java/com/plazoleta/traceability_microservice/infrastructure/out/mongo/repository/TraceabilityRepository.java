package com.plazoleta.traceability_microservice.infrastructure.out.mongo.repository;

import com.plazoleta.traceability_microservice.infrastructure.out.mongo.entity.TraceabilityDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TraceabilityRepository extends MongoRepository<TraceabilityDocument, String> {
    List<TraceabilityDocument> findByOrderIdAndCustomerId(Long orderId, Long customerId);
}
