package com.plazoleta.plazoleta_microservice.domain.model;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String identityNumber;
    private String phoneNumber;
    private String dateOfBirth;
    private String email;
    private String password;
    private String role;

    private User(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.identityNumber = builder.identityNumber;
        this.phoneNumber = builder.phoneNumber;
        this.dateOfBirth = builder.dateOfBirth;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
    }

    public String getDateOfBirth() {
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

    public String getRole() {
        return role;
    }

    public static class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private String identityNumber;
        private String phoneNumber;
        private String dateOfBirth;
        private String email;
        private String password;
        private String role;

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

        public Builder dateOfBirth(String dateOfBirth) {
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

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
