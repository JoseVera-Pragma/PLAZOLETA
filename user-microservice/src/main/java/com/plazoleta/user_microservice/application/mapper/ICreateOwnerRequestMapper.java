package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.application.dto.request.CreateOwnerRequestDto;
import com.plazoleta.user_microservice.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ICreateOwnerRequestMapper {
    User toUser(CreateOwnerRequestDto createOwnerRequestDto);
}
