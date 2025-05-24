package com.plazoleta.user_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {IValueObjectMapper.class})
public interface IUserEntityMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "identityNumber", source = "identityNumber")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    User toUser(UserEntity userEntity);

    UserEntity toUserEntity(User user);

    List<User> toUserList(List<UserEntity> userEntityList);
}
