package com.plazoleta.user_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUserEntityMapper {
    @Mapping(target = "role", source = "role")
    User toUser(UserEntity userEntity);

    UserEntity toUserEntity(User user);

    List<User> toUserList(List<UserEntity> userEntityList);
}
