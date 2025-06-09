package com.plazoleta.traceability_microservice.infrastructure.out.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "trazabilidad")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TraceabilityDocument {
    @Id
    private String id;
    private Long orderId;
    private Long customerId;
    private String customerEmail;
    private LocalDateTime date;
    private String previousState;
    private String newState;
    private Long employedId;
    private String employedEmail;
}
