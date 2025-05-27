package com.plazoleta.user_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.user_microservice.application.dto.request.UserRequestDto;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.UserEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IRoleRepository;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;

    @BeforeEach
    void setup() {
        RoleEntity roleEntityAdmin = new RoleEntity();
        roleEntityAdmin.setName(RoleList.ROLE_ADMIN);
        roleEntityAdmin.setDescription("Administrador");
        RoleEntity roleEntityOwner = new RoleEntity();
        roleEntityOwner.setName(RoleList.ROLE_OWNER);
        roleEntityOwner.setDescription("Propietario");

        RoleEntity savedRole = roleRepository.save(roleEntityAdmin);
        roleRepository.save(roleEntityOwner);

        UserEntity user = new UserEntity();
        user.setFirstName("First");
        user.setLastName("Last");
        user.setIdentityNumber("332112");
        user.setPhoneNumber("+5561651");
        user.setDateOfBirth(LocalDate.of(2007,1,1));
        user.setEmail("test@example.com");
        user.setPassword("123456");
        user.setRole(savedRole);

        userId = userRepository.save(user).getId();
    }

    @Test
    void createUser_ShouldReturn201() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setFirstName("Test");
        request.setLastName("Example");
        request.setIdentityNumber("213213212");
        request.setPhoneNumber("+56561521321");
        request.setDateOfBirth(LocalDate.of(2007,1,1));
        request.setEmail("teste5464@example.com");
        request.setPassword("password");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<UserEntity> users = userRepository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void getAllUsers_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].email", notNullValue()));
    }

    @Test
    void getUserById_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser_ShouldReturn200() throws Exception {
        UserRequestDto request = new UserRequestDto();

        request.setFirstName("Test");
        request.setLastName("Example");
        request.setIdentityNumber("213213212");
        request.setPhoneNumber("+56561521321");
        request.setDateOfBirth(LocalDate.of(2007,1,1));
        request.setEmail("teste@example.com");
        request.setPassword("password");

        mockMvc.perform(put("/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        UserEntity updated = userRepository.findById(userId).orElseThrow();
        assertEquals("Test", updated.getFirstName());
    }

    @Test
    void deleteUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/v1/users/" + userId))
                .andExpect(status().isNoContent());

        assertEquals(0, userRepository.findAll().size());
    }
}