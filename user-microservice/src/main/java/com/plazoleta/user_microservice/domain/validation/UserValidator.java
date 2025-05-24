package com.plazoleta.user_microservice.domain.validation;

import com.plazoleta.user_microservice.domain.exception.RoleNotAllowedException;
import com.plazoleta.user_microservice.domain.exception.UnderAgeOwnerException;
import com.plazoleta.user_microservice.domain.exception.UserAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class UserValidator {
    private final IUserPersistencePort userPersistencePort;

    public UserValidator(IUserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    public void validateUserCreation(User newUser, Role creatorRole) {
        validateRoleCreationPermissions(newUser.getRole(), creatorRole);
        validateUniqueEmail(newUser.getEmail());
        validateOwnerAge(newUser);
    }

    public void validateUserUpdate(User user, Role updaterRole) {
        validateUserExists(user.getId());
        validateEmailNotTakenByAnother(user.getEmail(), user.getId());
        validateRoleUpdatePermissions(user.getRole(), updaterRole);
        validateOwnerAge(user);
    }

    public void validateUserDelete(Long userId, Role deleterRole) {
        validateUserExists(userId);
        validateRoleDeletePermissions(userId, deleterRole);
    }

    private void validateUniqueEmail(Email email) {
        if (userPersistencePort.getUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("Email already exists: " + email.getValue());
        }
    }

    private void validateOwnerAge(User user) {
        if (user.getRole().isOwner()) {
            LocalDate dob = user.getDateOfBirth();
            if (dob == null) {
                throw new UnderAgeOwnerException("Date of birth is required for owners.");
            }
            int age = Period.between(dob, LocalDate.now()).getYears();
            if (age < 18) {
                throw new UnderAgeOwnerException("Owner must be at least 18 years old.");
            }
        }
    }

    private void validateRoleCreationPermissions(Role newUserRole, Role creatorRole) {
        if (creatorRole.isAdmin() && !newUserRole.isOwner()) {
            throw new RoleNotAllowedException("Admin can only create Owner");
        }
        if (creatorRole.isOwner() && !newUserRole.isEmployed()) {
            throw new RoleNotAllowedException("Owner can only create Employed");
        }
        if (creatorRole.isCustomer()) {
            throw new RoleNotAllowedException("Customers cannot create other users");
        }
    }

    private void validateUserExists(Long userId) {
        if (userPersistencePort.getUser(userId) == null) {
            throw new UserNotFoundException("Cannot update: User not found with ID: " + userId);
        }
    }

    private void validateEmailNotTakenByAnother(Email email, Long userId) {
        User userByEmail = userPersistencePort.getUserByEmail(email);
        if (userByEmail != null && !userByEmail.getId().equals(userId)) {
            throw new UserAlreadyExistsException("Email already exists: " + email.getValue());
        }
    }

    private void validateRoleUpdatePermissions(Role newRole, Role updaterRole) {
        if (updaterRole.isAdmin() && !newRole.isOwner()) {
            throw new RoleNotAllowedException("Admin can only assign Owner role");
        }
        if (updaterRole.isOwner() && !newRole.isEmployed()) {
            throw new RoleNotAllowedException("Owner can only assign Employed role");
        }
        if (updaterRole.isCustomer()) {
            throw new RoleNotAllowedException("Customers cannot update roles");
        }
    }

    private void validateRoleDeletePermissions(Long id, Role deleterRole) {
        User user = userPersistencePort.getUser(id);
        Role userRole = user.getRole();
        if (deleterRole.isAdmin() && !userRole.isOwner()) {
            throw new RoleNotAllowedException("Admin can only delete users with Owner role");
        }
        if (deleterRole.isOwner() && !userRole.isEmployed()) {
            throw new RoleNotAllowedException("Owner can only delete users with Employed role");
        }
        if (deleterRole.isCustomer()) {
            throw new RoleNotAllowedException("Customers cannot delete users");
        }
    }


}
