package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.application.dto.response.AuthResponseDto;
import com.plazoleta.user_microservice.domain.model.AuthToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IAuthRequestMapper {

    @Mapping(source = "token", target = "accessToken")
    @Mapping(source = "type", target = "tokenType")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "userId", target = "userId")
    AuthResponseDto toAuthResponse(AuthToken authToken);
}