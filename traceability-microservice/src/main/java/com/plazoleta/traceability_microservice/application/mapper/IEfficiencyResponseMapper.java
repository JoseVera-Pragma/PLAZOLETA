package com.plazoleta.traceability_microservice.application.mapper;

import com.plazoleta.traceability_microservice.application.dto.response.EfficiencyResponseDto;
import com.plazoleta.traceability_microservice.domain.model.EfficiencyReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IEfficiencyResponseMapper {
    @Mapping(target = "durationInMinutes", expression = "java(efficiencyReport.getDuration().toMinutes())")
    EfficiencyResponseDto toResponseDto(EfficiencyReport efficiencyReport);
}
