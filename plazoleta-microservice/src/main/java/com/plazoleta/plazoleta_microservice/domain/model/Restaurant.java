package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.exception.*;

import java.util.Objects;

public class Restaurant {
    private Long id;
    private String name;
    private Nit nit;
    private String address;
    private PhoneNumber phoneNumber;
    private String urlLogo;
    private Long idOwner;

    private Restaurant(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.nit = new Nit(builder.nit);
        this.address = builder.address;
        this.phoneNumber = new PhoneNumber(builder.phoneNumber);
        this.urlLogo = builder.urlLogo;
        this.idOwner = builder.idOwner;
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
            if (name == null || name.isBlank()) {
                throw new InvalidRestaurantNameException("Restaurant name must not be null or empty");
            }
            this.name = name;
            return this;
        }

        public Builder nit(String nit) {
            if (nit == null || nit.isBlank()) {
                throw new InvalidRestaurantNitException("Restaurant NIT must not be null or empty");
            }
            this.nit = nit;
            return this;
        }

        public Builder address(String address) {
            if (address == null || address.isBlank()) {
                throw new InvalidRestaurantAddressException("Restaurant address must not be null or empty");
            }
            this.address = address;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.isBlank()) {
                throw new MissingPhoneNumberException("Phone number must not be null or empty");
            }
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder urlLogo(String urlLogo) {
            if (urlLogo == null || urlLogo.isBlank()) {
                throw new InvalidRestaurantUrlLogoException("Restaurant logo URL must not be null or empty");
            }
            this.urlLogo = urlLogo;
            return this;
        }

        public Builder idOwner(Long idOwner) {
            if (idOwner == null) {
                throw new InvalidRestaurantOwnerIdException("Owner ID must not be null");
            }
            this.idOwner = idOwner;
            return this;
        }

        public Restaurant build() {
            if (name == null || nit == null || address == null || phoneNumber == null || urlLogo == null || idOwner == null) {
                throw new DomainException("All required fields must be provided");
            }
            return new Restaurant(this);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(Long idOwner) {
        this.idOwner = idOwner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNit() {
        return nit.getValue();
    }

    public void setNit(String nit) {
        this.nit = new Nit(nit);
    }

    public String getPhone() {
        return phoneNumber.getValue();
    }

    public void setPhone(String phoneNumber) {
        this.phoneNumber = new PhoneNumber(phoneNumber);
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
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
