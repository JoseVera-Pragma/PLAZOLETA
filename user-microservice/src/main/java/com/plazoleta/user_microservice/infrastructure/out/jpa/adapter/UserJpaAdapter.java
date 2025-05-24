package com.plazoleta.user_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.user_microservice.domain.exception.UserAlreadyExistsException;
import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository iUserRepository;
    private final IUserEntityMapper iUserEntityMapper;

    @Override
    public User saveUser(User user) {
        if (iUserRepository.existsByEmail(user.getEmail().getValue())) {
            throw new UserAlreadyExistsException("User already exists with email: " + user.getEmail().getValue());
        }
        UserEntity entity = iUserEntityMapper.toUserEntity(user);
        UserEntity save = iUserRepository.save(entity);
        return iUserEntityMapper.toUser(save);
    }

    @Override
    public User getUser(Long id) {
        UserEntity entity = iUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));;
        return iUserEntityMapper.toUser(entity);
    }

    @Override
    public User getUserByEmail(Email email) {
        UserEntity entity = iUserRepository.findByEmail(email.getValue());
        return iUserEntityMapper.toUser(entity);
    }

    @Override
    public List<User> getAllUsers() {
        List<UserEntity> userEntityList = iUserRepository.findAll();
        return iUserEntityMapper.toUserList(userEntityList);
    }

    @Override
    public void updateUser(User user) {
        if (!iUserRepository.existsById(user.getId())) {
            throw new UserNotFoundException("Cannot update user. User not found with id: " + user.getId());
        }
        UserEntity entity = iUserEntityMapper.toUserEntity(user);
        iUserRepository.save(entity);
    }

    @Override
    public void deleteUser(Long id) {
        if (!iUserRepository.existsById(id)) {
            throw new UserNotFoundException("Cannot delete user. User not found with id: " + id);
        }
        iUserRepository.deleteById(id);
    }

}
