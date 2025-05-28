package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.PhoneNumber;
import com.plazoleta.plazoleta_microservice.domain.model.RestaurantName;
import com.plazoleta.plazoleta_microservice.domain.model.RestaurantNit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IValueObjectMapper {

    default RestaurantName toRestaurantName(String value) {
        return value == null ? null : new RestaurantName(value);
    }

    default String fromRestaurantName(RestaurantName name) {
        return name == null ? null : name.getValue();
    }

    default RestaurantNit toRestaurantNit(String value) {
        return value == null ? null : new RestaurantNit(value);
    }

    default String fromRestaurantNit(RestaurantNit nit) {
        return nit == null ? null : nit.getValue();
    }

    default PhoneNumber toPhoneNumber(String value) {
        return value == null ? null : new PhoneNumber(value);
    }

    default String fromPhoneNumber(PhoneNumber phone) {
        return phone == null ? null : phone.getValue();
    }
}