package com.plazoleta.plazoleta_microservice.domain.validator;

import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.*;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;

public class RestaurantValidator {

    private RestaurantValidator() {}

    public static void validateRestaurant(Restaurant restaurant) {
        validateName(restaurant.getName());
        validateNit(restaurant.getNit());
        validateAddress(restaurant.getAddress());
        validatePhoneNumber(restaurant.getPhoneNumber());
        validateUrlLogo(restaurant.getUrlLogo());
        validateIdOwner(restaurant.getIdOwner());
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidRestaurantNameException("Restaurant name must not be null or empty");
        } else if (name.matches("^\\d+$")) {
            throw new InvalidRestaurantNameException("Restaurant name must not contain only numbers");
        }
    }

    private static void validateNit(String nit) {
        if (nit == null || nit.isBlank()) {
            throw new MissingNitException("Restaurant NIT must be provided");
        } else if (!nit.matches("^\\d+$")) {
            throw new InvalidRestaurantNitException("Restaurant NIT is not valid: " + nit);
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new InvalidRestaurantAddressException("Restaurant address must not be null or empty");
        }
    }

    private static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new MissingPhoneNumberException("Phone number must be provided");
        } else if (!phoneNumber.matches("^\\+?\\d{1,13}$")) {
            throw new InvalidPhoneNumberException("Phone number is not valid: " + phoneNumber);
        }
    }

    private static void validateUrlLogo(String urlLogo) {
        if (urlLogo == null || urlLogo.isBlank()) {
            throw new InvalidRestaurantUrlLogoException("Restaurant logo URL must not be null or empty");
        }
    }

    private static void validateIdOwner(Long idOwner) {
        if (idOwner == null) {
            throw new InvalidRestaurantOwnerIdException("Owner ID must not be null");
        }
    }
}