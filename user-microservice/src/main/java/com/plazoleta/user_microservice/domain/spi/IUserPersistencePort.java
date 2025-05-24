package com.plazoleta.user_microservice.domain.spi;

import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserPersistencePort {

    User saveUser(User user);

    Optional<User> getUser(Long id);

    Optional<User> getUserByEmail(Email email);

    List<User> getAllUsers();

    void updateUser(User user);

    void deleteUser(Long id);
}
