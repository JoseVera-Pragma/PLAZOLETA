package com.plazoleta.traceability_microservice.infrastructure.out.mongo.mapper;

import com.plazoleta.traceability_microservice.domain.model.Traceability;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.entity.TraceabilityDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TraceabilityDocumentMapper {
    Traceability toTraceability(TraceabilityDocument traceabilityDocument);
    TraceabilityDocument toTraceabilityDocument(Traceability traceability);
}
