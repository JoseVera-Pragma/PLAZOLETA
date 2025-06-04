package com.plazoleta.user_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository iUserRepository;
    private final IUserEntityMapper iUserEntityMapper;

    @Override
    public void saveUser(User user) {
        UserEntity entity = iUserEntityMapper.toUserEntity(user);
        iUserRepository.save(entity);
    }

    @Override
    public Optional<User> getUser(Long id) {
        return iUserRepository.findById(id)
                .map(iUserEntityMapper::toUser);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return iUserRepository.findByEmail(email)
                .map(iUserEntityMapper::toUser);
    }

    @Override
    public List<User> getAllUsers() {
        List<UserEntity> userEntityList = iUserRepository.findAll();
        return iUserEntityMapper.toUserList(userEntityList);
    }

    @Override
    public void deleteUser(Long id) {
        iUserRepository.deleteById(id);
    }

}
