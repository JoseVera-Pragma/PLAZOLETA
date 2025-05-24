package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.exception.*;
import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.domain.validation.UserValidator;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;
    private final UserValidator userValidator;

    public UserUseCase(IUserPersistencePort userPersistencePort, IRolePersistencePort rolePersistencePort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
        this.userValidator = new UserValidator(userPersistencePort);
    }

    @Override
    public User saveUser(User newUser, Role creatorRole) {
        Role newUserRole = rolePersistencePort.getRoleByName(newUser.getRole().getName())
                .orElseThrow(() -> new RoleNotFoundException("Role not found with Name: " + newUser.getRole().getName()));

        newUser.setRole(newUserRole);

        userValidator.validateUserCreation(newUser, creatorRole);

        return userPersistencePort.saveUser(newUser);
    }

    @Override
    public User getUser(Long id) {
        return userPersistencePort.getUser(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    @Override
    public User getUserByEmail(Email email) {
        return userPersistencePort.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<User> getAllUsers() {
        return userPersistencePort.getAllUsers();
    }

    @Override
    public void updateUser(User user,Role creatorRole) {
        userValidator.validateUserUpdate(user, creatorRole);
        userPersistencePort.updateUser(user);
    }

    @Override
    public void deleteUser(Long id) {

        if (userPersistencePort.getUser(id).isEmpty()) {
            throw new UserNotFoundException("Cannot delete: User not found with ID: " + id);
        }
        userPersistencePort.deleteUser(id);
    }

}
