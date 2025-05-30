package com.plazoleta.user_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.user_microservice.application.dto.request.UserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.handler.IUserHandler;
import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class SimpleUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenAdapter jwtTokenAdapter;

    @MockitoBean
    private IUserHandler userHandler;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .firstName("Super")
                .lastName("Admin")
                .identityNumber(new IdentityNumber("999999999"))
                .dateOfBirth(LocalDate.of(1990,1,1))
                .phoneNumber(new PhoneNumber("+573000000000"))
                .email(new Email("admin@plazoleta.com"))
                .password("encrypted")
                .role(new Role(1L, RoleList.ROLE_ADMIN, "Administrador"))
                .build();

        adminToken = jwtTokenAdapter.generateToken(user);
    }

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        UserRequestDto userDto = new UserRequestDto("Jose", "Perez", "123456", "321654987", LocalDate.of(1999, 5, 5), "jose@email.com", "pass123");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllUsers_withAdminRole_shouldReturnOk() throws Exception {
        List<UserResponseDto> mockUsers = List.of(
                new UserResponseDto("Jose", "admin@email.com", "123123123", "2131321", LocalDate.of(2000, 1, 1), "admin@plazoleta.com", "psww","ADMIN")
        );

        Mockito.when(userHandler.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@plazoleta.com"));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        Long userId = 1L;
        UserResponseDto user = new UserResponseDto("Jose", "admin@email.com", "123123123", "2131321", LocalDate.of(2000, 1, 1), "admin@plazoleta.com", "psww","ADMIN");

        Mockito.when(userHandler.getUser(userId)).thenReturn(user);

        mockMvc.perform(get("/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@plazoleta.com"));
    }

    @Test
    void getUserByEmail_shouldReturnUser() throws Exception {
        String email = "admin@plazoleta.com";
        UserResponseDto user = new UserResponseDto("Jose", "admin@email.com", "123123123", "2131321", LocalDate.of(2000, 1, 1), email, "psww","ADMIN");

        Mockito.when(userHandler.getUserByEmail(email)).thenReturn(user);

        mockMvc.perform(get("/users/email/" + email)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void updateUser_shouldReturnOk() throws Exception {
        Long userId = 1L;
        UserRequestDto updatedUser = new UserRequestDto("Jose", "Perez", "123123123", "2131321", LocalDate.of(2000, 1, 1), "admin@plazoleta.com", "psww");

        mockMvc.perform(put("/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}