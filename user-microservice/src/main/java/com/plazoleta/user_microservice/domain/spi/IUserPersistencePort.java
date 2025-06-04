package com.plazoleta.user_microservice.domain.spi;

import com.plazoleta.user_microservice.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserPersistencePort {

    void saveUser(User user);

    Optional<User> getUser(Long id);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();

    void deleteUser(Long id);
}
