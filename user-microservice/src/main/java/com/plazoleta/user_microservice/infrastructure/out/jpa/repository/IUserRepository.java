package com.plazoleta.user_microservice.infrastructure.out.jpa.repository;

import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByRoleId(Long roleId);
}
