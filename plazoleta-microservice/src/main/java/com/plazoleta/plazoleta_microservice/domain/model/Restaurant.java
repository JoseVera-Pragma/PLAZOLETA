package com.plazoleta.plazoleta_microservice.domain.model;

import java.util.Objects;

public class Restaurant {
    private final Long id;
    private final String name;
    private final String nit;
    private final String address;
    private final String phoneNumber;
    private final String urlLogo;
    private final Long idOwner;

    public Restaurant(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.nit = builder.nit;
        this.address = builder.address;
        this.phoneNumber = builder.phoneNumber;
        this.urlLogo = builder.urlLogo;
        this.idOwner = builder.idOwner;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String nit;
        private String address;
        private String phoneNumber;
        private String urlLogo;
        private Long idOwner;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder nit(String nit) {
            this.nit = nit;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder urlLogo(String urlLogo) {
            this.urlLogo = urlLogo;
            return this;
        }

        public Builder idOwner(Long idOwner) {
            this.idOwner = idOwner;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }

    public String getAddress() {
        return address;
    }

    public Long getId() {
        return id;
    }

    public Long getIdOwner() {
        return idOwner;
    }

    public String getName() {
        return name;
    }

    public String getNit() {
        return nit;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
