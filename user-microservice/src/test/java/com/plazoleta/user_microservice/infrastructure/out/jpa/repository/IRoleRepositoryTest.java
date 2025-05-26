package com.plazoleta.user_microservice.infrastructure.out.jpa.repository;

import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IRoleRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IRoleRepository roleRepository;

    @Test
    void shouldFindRoleByName() {
        RoleEntity role = new RoleEntity();
        role.setName(RoleList.ROLE_ADMIN);
        role.setDescription("Administrador");
        entityManager.persist(role);
        entityManager.flush();

        RoleEntity found = roleRepository.findByName(RoleList.ROLE_ADMIN);
        assertNotNull(found);
        assertEquals(RoleList.ROLE_ADMIN, found.getName());
    }

    @Test
    void shouldReturnTrueIfRoleExistsByName() {
        RoleEntity role = new RoleEntity(null, RoleList.ROLE_OWNER, "Propietario");
        entityManager.persist(role);
        entityManager.flush();

        boolean exists = roleRepository.existsByName(RoleList.ROLE_OWNER);
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseIfRoleDoesNotExist() {
        boolean exists = roleRepository.existsByName(RoleList.ROLE_CUSTOMER);
        assertFalse(exists);
    }
}