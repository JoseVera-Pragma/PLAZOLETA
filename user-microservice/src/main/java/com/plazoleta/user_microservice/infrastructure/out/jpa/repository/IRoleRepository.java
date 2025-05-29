package com.plazoleta.user_microservice.infrastructure.out.jpa.repository;

import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepository extends JpaRepository <RoleEntity, Long> {
    RoleEntity findByName(RoleList name);
    boolean existsByName(RoleList name);
}
