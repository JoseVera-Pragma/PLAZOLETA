package com.plazoleta.user_microservice.infrastructure.out.jpa.repository;

import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRoleId(Long roleId);
}
