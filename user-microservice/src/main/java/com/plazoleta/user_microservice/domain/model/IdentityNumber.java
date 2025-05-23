package com.plazoleta.user_microservice.domain.model;

import java.util.Objects;

public class IdentityNumber {

    private final String value;

    public IdentityNumber(String value) {
        if (value == null || value.isBlank() || !value.matches("^\\d+$")) {
            throw new IllegalArgumentException("Identity number is invalid, only can contain numbers.");
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
        IdentityNumber that = (IdentityNumber) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
