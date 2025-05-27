package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.exception.InvalidNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.MissingNitException;

import java.util.Objects;

public class RestaurantNit {
    private final String value;

    public RestaurantNit(String value) {
        if (value == null || value.isBlank()) {
            throw new MissingNitException("Nit must be provided");
        } else if (!value.matches("^\\d+$")) {
            throw new InvalidNitException("Nit is not valid: " + value);
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
        RestaurantNit that = (RestaurantNit) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
