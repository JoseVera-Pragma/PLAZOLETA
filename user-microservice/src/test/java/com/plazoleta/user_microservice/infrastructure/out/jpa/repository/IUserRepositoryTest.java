package com.plazoleta.user_microservice.infrastructure.out.jpa.repository;

import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IUserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    IUserRepository userRepository;

    @Test
    void shouldReturnUserWhenEmailExists(){

        RoleEntity role = new RoleEntity();
        role.setName(RoleList.ROLE_ADMIN);
        role.setDescription("Administrador");

        entityManager.persist(role);
        entityManager.flush();

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("First");
        userEntity.setLastName("Last");
        userEntity.setIdentityNumber("12231321321");
        userEntity.setDateOfBirth(LocalDate.of(2007,1,1));
        userEntity.setPhoneNumber("+3151651519");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("password");
        userEntity.setRole(role);

        entityManager.persist(userEntity);
        entityManager.flush();

        UserEntity found = userRepository.findByEmail("test@email.com");

        assertNotNull(found);
        assertEquals(userEntity,found);
    }

    @Test
    void shouldReturnTrueWhenExistsUserByEmail(){

        RoleEntity role = new RoleEntity();
        role.setName(RoleList.ROLE_ADMIN);
        role.setDescription("Administrador");

        entityManager.persist(role);
        entityManager.flush();

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("First");
        userEntity.setLastName("Last");
        userEntity.setIdentityNumber("12231321321");
        userEntity.setDateOfBirth(LocalDate.of(2007,1,1));
        userEntity.setPhoneNumber("+3151651519");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("password");
        userEntity.setRole(role);

        entityManager.persist(userEntity);
        entityManager.flush();

        assertTrue(userRepository.existsByEmail("test@email.com"));
    }

    @Test
    void shouldReturnTrueWhenExistUserById(){

        RoleEntity role = new RoleEntity();
        role.setName(RoleList.ROLE_ADMIN);
        role.setDescription("Administrador");

        entityManager.persist(role);
        entityManager.flush();

        Long roleId = role.getId();

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("First");
        userEntity.setLastName("Last");
        userEntity.setIdentityNumber("12231321321");
        userEntity.setDateOfBirth(LocalDate.of(2007,1,1));
        userEntity.setPhoneNumber("+3151651519");
        userEntity.setEmail("test@email.com");
        userEntity.setPassword("password");
        userEntity.setRole(role);

        entityManager.persist(userEntity);
        entityManager.flush();

        assertTrue(userRepository.existsByRoleId(roleId));
    }

}