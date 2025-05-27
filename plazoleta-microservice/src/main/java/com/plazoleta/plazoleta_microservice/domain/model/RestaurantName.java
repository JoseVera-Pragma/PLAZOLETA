package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.exception.InvalidNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.InvalidRestaurantNameException;

import java.util.Objects;

public class RestaurantName {
    private final String value;

    public RestaurantName(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidRestaurantNameException("Name must not be null or empty");
        } else if (value.matches("^\\d+$")) {
            throw new InvalidNitException("Name must not contain only numbers");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantName that = (RestaurantName) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
