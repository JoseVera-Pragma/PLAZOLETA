package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.api.IUserServicePort;
import com.plazoleta.user_microservice.domain.exception.RoleNotFoundException;
import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IAuthenticatedUserPort;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.domain.validation.UserValidator;

import java.util.List;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final IPasswordEncoderPort passwordEncoder;
    private final IRolePersistencePort iRolePersistencePort;
    private final UserValidator userValidator;

    public UserUseCase(IAuthenticatedUserPort authenticatedUserPort, IUserPersistencePort userPersistencePort, IPasswordEncoderPort passwordEncoder, IRolePersistencePort iRolePersistencePort, UserValidator userValidator) {
        this.authenticatedUserPort = authenticatedUserPort;
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoder = passwordEncoder;
        this.iRolePersistencePort = iRolePersistencePort;
        this.userValidator = userValidator;
    }

    @Override
    public void saveCustomerUser(User newUser) {
        Role creatorRole = authenticatedUserPort.getCurrentUserRole();
        Role role = iRolePersistencePort.getRoleByName(RoleList.ROLE_CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException("Customer role not found"));

        User securedUser = newUser.withPasswordAndRole(
                passwordEncoder.encode(newUser.getPassword()),
                role
        );
        userValidator.validateUserCreation(securedUser, creatorRole);
        userPersistencePort.saveUser(securedUser);
    }

    @Override
    public void saveEmployedUser(User newUser) {
        Role creatorRole = authenticatedUserPort.getCurrentUserRole();
        Role role = iRolePersistencePort.getRoleByName(RoleList.ROLE_EMPLOYED)
                .orElseThrow(() -> new RoleNotFoundException("Employed role not found"));

        User securedUser = newUser.withPasswordAndRole(
                passwordEncoder.encode(newUser.getPassword()),
                role
        );
        userValidator.validateUserCreation(securedUser, creatorRole);
        userPersistencePort.saveUser(securedUser);
    }

    @Override
    public void saveOwnerUser(User newUser) {
        Role creatorRole = authenticatedUserPort.getCurrentUserRole();
        Role role = iRolePersistencePort.getRoleByName(RoleList.ROLE_OWNER)
                .orElseThrow(() -> new RoleNotFoundException("Owner role not found"));

        User securedUser = newUser.withPasswordAndRole(
                passwordEncoder.encode(newUser.getPassword()),
                role
        );
        userValidator.validateUserCreation(securedUser, creatorRole);
        userPersistencePort.saveUser(securedUser);
    }

    @Override
    public User getUser(Long id) {
        return userPersistencePort.getUser(id).orElseThrow(
                () -> new UserNotFoundException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userPersistencePort.getUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userPersistencePort.getAllUsers();
    }

    @Override
    public void updateUser(Long id, User user) {
        userPersistencePort.getUser(id).orElseThrow(
                () -> new UserNotFoundException("User not found"));
        User updated = user.withId(id);
        userPersistencePort.saveUser(updated);
    }

    @Override
    public void deleteUser(Long id) {
        userPersistencePort.getUser(id).orElseThrow(
                () -> new UserNotFoundException("User not found"));

        userPersistencePort.deleteUser(id);
    }
}
