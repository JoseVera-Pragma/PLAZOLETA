package com.plazoleta.traceability_microservice.application.mapper;

import com.plazoleta.traceability_microservice.application.dto.response.EmployeeEfficiencyRankingResponseDto;
import com.plazoleta.traceability_microservice.domain.model.EmployeeEfficiencyRanking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IEmployeeEfficiencyRankingResponseMapper {
    @Mapping(target = "durationInMinutes", expression = "java(model.getDuration().toMinutes())")
    EmployeeEfficiencyRankingResponseDto toDto(EmployeeEfficiencyRanking model);
}