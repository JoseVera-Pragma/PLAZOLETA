package com.plazoleta.traceability_microservice.application.mapper;

import com.plazoleta.traceability_microservice.application.dto.request.TraceabilityRequestDto;
import com.plazoleta.traceability_microservice.domain.model.Traceability;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TraceabilityRequestMapper {
    Traceability toTraceability(TraceabilityRequestDto traceabilityRequestDto);
}
