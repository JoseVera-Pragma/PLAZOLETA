package com.plazoleta.user_microservice.domain.model;

import java.util.Objects;

public class PhoneNumber {
    private final String value;

    public PhoneNumber(String value) {
        if (value == null || value.isBlank() || !value.matches("^\\+?\\d{1,13}$")) {
            throw new IllegalArgumentException("Phone number is not valid: " + value);
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
        PhoneNumber that = (PhoneNumber) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
