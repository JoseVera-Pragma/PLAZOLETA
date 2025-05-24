package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.exception.UserAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;

import java.util.List;
import java.util.Optional;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;

    public UserUseCase(IUserPersistencePort userPersistencePort, IRolePersistencePort rolePersistencePort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
    }

    @Override
    public User saveUser(User user) {
        Role role = rolePersistencePort.getRole(user.getRole().getId())
                .orElseThrow(() -> new RoleNotFoundException("Role not found with ID: " + user.getRole().getId()));

        if (userPersistencePort.getUserByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("A user with email " + user.getEmail().getValue() + " already exists.");
        }

        user.setRole(role);
        return userPersistencePort.saveUser(user);
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
    public void updateUser(User user) {
        Optional<User> existingUser = userPersistencePort.getUser(user.getId());
        if (existingUser.isEmpty()) {
            throw new UserNotFoundException("Cannot update: User not found with ID: " + user.getId());
        }

        Optional<User> userByEmail = userPersistencePort.getUserByEmail(user.getEmail());

        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(user.getId())) {
            throw new UserAlreadyExistsException("Cannot update: Email is already used by another user.");
        }

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
