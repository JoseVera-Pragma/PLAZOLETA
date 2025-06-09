package com.plazoleta.plazoleta_microservice.infrastructure.out.feign.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.Traceability;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.dto.TraceabilityRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ITraceabilityRequestMapper {
    Traceability toTraceability (TraceabilityRequestDto traceabilityRequestDto);

    TraceabilityRequestDto toTraceabilityRequest(Traceability traceability);
}
