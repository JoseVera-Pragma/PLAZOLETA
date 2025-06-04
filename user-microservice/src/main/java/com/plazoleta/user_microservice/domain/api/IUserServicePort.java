package com.plazoleta.user_microservice.domain.api;

import com.plazoleta.user_microservice.domain.model.User;

import java.util.List;

public interface IUserServicePort {

    void saveCustomerUser(User newUser);

    void saveEmployedUser(User newUser);

    void saveOwnerUser(User newUser);

    User getUser(Long id);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    void updateUser(Long id,User user);

    void deleteUser(Long id);
}
