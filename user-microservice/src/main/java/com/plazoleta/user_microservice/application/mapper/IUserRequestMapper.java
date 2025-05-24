package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.application.dto.request.UserRequestDto;
import com.plazoleta.user_microservice.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {ValueObjectMapper.class})
public interface IUserRequestMapper {

    @Mapping(target = "email", source = "email", qualifiedByName = "toEmail")
    @Mapping(target = "identityNumber", source = "identityNumber", qualifiedByName = "toIdentityNumber")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "toPhoneNumber")
    User toUser(UserRequestDto userRequestDto);

    @Named("toEmail")
    default Email toEmail(String email) {
        return new Email(email);
    }

    @Named("toIdentityNumber")
    default IdentityNumber toIdentityNumber(String identityNumber) {
        return new IdentityNumber(identityNumber);
    }
    @Named("toPhoneNumber")
    default PhoneNumber toPhoneNumber(String phoneNumber) {
        return new PhoneNumber(phoneNumber);
    }

}
