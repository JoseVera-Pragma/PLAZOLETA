package com.plazoleta.traceability_microservice.application.mapper;

import com.plazoleta.traceability_microservice.application.dto.response.TraceabilityResponseDto;
import com.plazoleta.traceability_microservice.domain.model.Traceability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TraceabilityResponseMapper {
    TraceabilityResponseDto toTraceabilityResponseDto(Traceability traceability);
}
