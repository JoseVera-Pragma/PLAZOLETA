package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.IdentityNumber;
import com.plazoleta.user_microservice.domain.model.PhoneNumber;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ValueObjectMapper {

    default Email toEmail(String value) {
        return new Email(value);
    }

    default String fromEmail(Email email) {
        return email.getValue();
    }

    default IdentityNumber toIdentityNumber(String value) {
        return new IdentityNumber(value);
    }

    default String fromIdentityNumber(IdentityNumber value) {
        return value.getValue();
    }

    default PhoneNumber toPhoneNumber(String value) {
        return new PhoneNumber(value);
    }

    default String fromPhoneNumber(PhoneNumber value) {
        return value.getValue();
    }
}