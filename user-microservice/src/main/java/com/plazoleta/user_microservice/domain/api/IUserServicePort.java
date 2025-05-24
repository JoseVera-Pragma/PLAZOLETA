package com.plazoleta.user_microservice.domain.api;

import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.Role;
import com.plazoleta.user_microservice.domain.model.User;

import java.util.List;

public interface IUserServicePort {

    User saveUser(User user, Role creatorRole);

    User getUser(Long id);

    User getUserByEmail(Email email);

    List<User> getAllUsers();

    void updateUser(User user, Role creatorRole);

    void deleteUser(Long id);
}
