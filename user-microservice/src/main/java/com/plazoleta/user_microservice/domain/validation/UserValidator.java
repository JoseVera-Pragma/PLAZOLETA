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

    public void validateUserDelete(User user, Role deleterRole) {
        if (user == null){
            throw new UserNotFoundException("User not found");
        }
        validateUserExists(user.getId());
        validateRoleDeletionPermissions(user.getRole(), deleterRole);
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
        if (newRole.isOwner() && !updaterRole.isAdmin()) {
            throw new RoleNotAllowedException("Only ADMIN can assign the OWNER role.");
        }

        if (newRole.isEmployed() && !updaterRole.isOwner()) {
            throw new RoleNotAllowedException("Only OWNER can assign the EMPLOYED role.");
        }

        if (newRole.isAdmin() && !updaterRole.isAdmin()) {
            throw new RoleNotAllowedException("Only ADMIN can assign the ADMIN role.");
        }
    }

    private void validateRoleDeletionPermissions(Role targetRole, Role requesterRole) {
        if (targetRole.isAdmin()) {
            throw new RoleNotAllowedException("ADMIN users cannot be deleted.");
        }

        if (targetRole.isOwner() && !requesterRole.isAdmin()) {
            throw new RoleNotAllowedException("Only ADMIN can delete an OWNER.");
        }

        if (targetRole.isEmployed() && !requesterRole.isOwner()) {
            throw new RoleNotAllowedException("Only OWNER can delete an EMPLOYED.");
        }
    }
}
