package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.exception.*;
import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.domain.validation.UserValidator;

import java.util.List;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final UserValidator userValidator;

    public UserUseCase(IUserPersistencePort userPersistencePort, UserValidator userValidator) {
        this.userPersistencePort = userPersistencePort;
        this.userValidator = userValidator;
    }

    @Override
    public User saveUser(User newUser, Role creatorRole) {
        userValidator.validateUserCreation(newUser, creatorRole);
        return userPersistencePort.saveUser(newUser);
    }

    @Override
    public User getUser(Long id) {
        return userPersistencePort.getUser(id);
    }

    @Override
    public User getUserByEmail(Email email) {
        return userPersistencePort.getUserByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userPersistencePort.getAllUsers();
    }

    @Override
    public void updateUser(User user,Role updaterRole) {
        userValidator.validateUserUpdate(user, updaterRole);
        userPersistencePort.updateUser(user);
    }

    @Override
    public void deleteUser(Long id, Role roleEliminator) {
        User deleteUser = userPersistencePort.getUser(id);
        if (deleteUser == null) {
            throw new UserNotFoundException("Cannot delete: User not found with ID: " + id);
        }

        userValidator.validateUserUpdate(deleteUser, roleEliminator);

        userPersistencePort.deleteUser(id);
    }

}
