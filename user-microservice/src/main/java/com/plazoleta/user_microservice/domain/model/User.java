package com.plazoleta.user_microservice.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public class User {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String identityNumber;
    private final String phoneNumber;
    private final LocalDate dateOfBirth;
    private final String email;
    private final String password;
    private final Role role;
    private final Long restaurantId;

    private User(Builder builder){
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.identityNumber = builder.identityNumber;
        this.dateOfBirth = builder.dateOfBirth;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.password = builder.password;
        this.role = builder.role;
        this.restaurantId = builder.restaurantId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private String identityNumber;
        private String phoneNumber;
        private LocalDate dateOfBirth;
        private String email;
        private String password;
        private Role role;
        private Long restaurantId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder identityNumber(String identityNumber) {
            this.identityNumber = identityNumber;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder restaurantId(Long restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public User withPasswordAndRole(String encryptedPassword, Role newRole) {
        return User.builder()
                .id(this.id)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .identityNumber(this.identityNumber)
                .phoneNumber(this.phoneNumber)
                .dateOfBirth(this.dateOfBirth)
                .email(this.email)
                .password(encryptedPassword)
                .role(newRole)
                .restaurantId(this.restaurantId)
                .build();
    }

    public User withId(Long id) {
        return User.builder()
                .id(id)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .identityNumber(this.identityNumber)
                .phoneNumber(this.phoneNumber)
                .dateOfBirth(this.dateOfBirth)
                .email(this.email)
                .password(this.password)
                .role(this.role)
                .restaurantId(this.restaurantId)
                .build();
    }
}
