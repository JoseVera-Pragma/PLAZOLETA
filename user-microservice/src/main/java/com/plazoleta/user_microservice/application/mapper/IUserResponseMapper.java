package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {ValueObjectMapper.class})
public interface IUserResponseMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "identityNumber", source = "identityNumber")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "role", source = "role.name")
    UserResponseDto toUserResponseDto(User user);

    List<UserResponseDto> toResponseDtoList(List<User> users);
}
