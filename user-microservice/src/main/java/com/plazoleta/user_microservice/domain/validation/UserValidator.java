package com.plazoleta.user_microservice.domain.validation;

import com.plazoleta.user_microservice.domain.exception.RoleNotAllowedException;
import com.plazoleta.user_microservice.domain.exception.UnderAgeException;
import com.plazoleta.user_microservice.domain.exception.UserAlreadyExistsException;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;

import java.time.LocalDate;
import java.time.Period;

public class UserValidator {
    private final IUserPersistencePort userPersistencePort;
    private static final int MINIMUM_AGE = 18;

    public UserValidator(IUserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    public void validateUserCreation(User newUser, Role creatorRole) {
        validateEmail(newUser.getEmail());
        validateIdentityNumber(newUser.getIdentityNumber());
        validatePhoneNumber(newUser.getPhoneNumber());
        validateOwnerAge(newUser);
        validateUniqueEmail(newUser.getEmail());
        validateRestaurantId(newUser);
        validateRoleCreationPermissions(newUser.getRole(), creatorRole);
        validateRequiredFields(newUser);
    }

    public void validateRequiredFields(User newUser) {
        if (newUser.getFirstName() == null || newUser.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (newUser.getLastName() == null || newUser.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (newUser.getPassword() == null || newUser.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }
    }

    public void validateRestaurantId(User newUser) {
        if (newUser.getRole().isEmployed() && newUser.getRestaurantId() == null) {
            throw new IllegalArgumentException("An employed user must be associated with a restaurant ID.");
        }
    }

    public void validateUniqueEmail(String email) {
        if (userPersistencePort.getUserByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists: " + email);
        }
    }

    public void validateOwnerAge(User newUser) {
        if (newUser.getRole().isOwner()) {
            LocalDate dob = newUser.getDateOfBirth();
            if (dob == null) {
                throw new UnderAgeException("Date of birth is required.");
            }
            int age = Period.between(dob, LocalDate.now()).getYears();
            if (age < MINIMUM_AGE) {
                throw new UnderAgeException("Owner must be at least 18 years old.");
            }
        }
    }

    public void validateRoleCreationPermissions(Role newUserRole, Role creatorRole) {
        if (newUserRole.isOwner() && !creatorRole.isAdmin()) {
            throw new RoleNotAllowedException("Only ADMIN can create an OWNER.");
        }

        if (newUserRole.isEmployed() && !creatorRole.isOwner()) {
            throw new RoleNotAllowedException("Only OWNER can create an EMPLOYED.");
        }

        if (newUserRole.isAdmin() && !creatorRole.isAdmin()) {
            throw new RoleNotAllowedException("Creation of ADMIN users is not allowed.");
        }
    }

    public void validatePhoneNumber(String value) {
        if (value == null || value.isBlank() || !value.matches("^\\+?\\d{1,13}$")) {
            throw new IllegalArgumentException("Phone number is not valid: " + value);
        }
    }

    public void validateIdentityNumber(String value) {
        if (value == null || value.isBlank() || !value.matches("^\\d+$")) {
            throw new IllegalArgumentException("Identity number is invalid, only can contain numbers.");
        }
    }

    public void validateEmail(String value) {
        if (value == null || value.isBlank() || !value.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email is not valid: " + value);
        }
    }
}
