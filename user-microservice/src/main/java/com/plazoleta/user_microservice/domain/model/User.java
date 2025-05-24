package com.plazoleta.user_microservice.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private IdentityNumber identityNumber;
    private PhoneNumber phoneNumber;
    private LocalDate dateOfBirth;
    private Email email;
    private String password;
    private Role role;

    private User(Builder builder){
        if (builder.firstName == null || builder.firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (builder.lastName == null || builder.lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (builder.identityNumber == null) {
            throw new IllegalArgumentException("Identity number is required");
        }
        if (builder.phoneNumber == null) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (builder.email == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (builder.password == null || builder.password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.identityNumber = builder.identityNumber;
        this.dateOfBirth = builder.dateOfBirth;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.password = builder.password;
        this.role = builder.role;
    }

    public static Builder builder() {
        return new Builder();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IdentityNumber getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(IdentityNumber identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        private IdentityNumber identityNumber;
        private PhoneNumber phoneNumber;
        private LocalDate dateOfBirth;
        private Email email;
        private String password;
        private Role role;

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

        public Builder identityNumber(IdentityNumber identityNumber) {
            this.identityNumber = identityNumber;
            return this;
        }

        public Builder phoneNumber(PhoneNumber phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder email(Email email) {
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

        public User build() {
            return new User(this);
        }
    }
}
