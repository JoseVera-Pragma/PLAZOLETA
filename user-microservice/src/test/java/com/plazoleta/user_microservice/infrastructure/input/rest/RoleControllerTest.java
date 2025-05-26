package com.plazoleta.user_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.user_microservice.application.dto.request.RoleRequestDto;
import com.plazoleta.user_microservice.domain.model.RoleList;
import com.plazoleta.user_microservice.infrastructure.out.jpa.entity.RoleEntity;
import com.plazoleta.user_microservice.infrastructure.out.jpa.repository.IRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IRoleRepository roleRepository;

    @BeforeEach
    void setup() {
        roleRepository.deleteAll();
    }

    @Test
    void createRole_shouldPersistInDatabase() throws Exception {
        RoleRequestDto request = new RoleRequestDto();
        request.setName("ROLE_OWNER");
        request.setDescription("Propietario");

        mockMvc.perform(post("/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<RoleEntity> roles = roleRepository.findAll();
        assertEquals(1, roles.size());
        assertEquals(RoleList.ROLE_OWNER, roles.get(0).getName());
    }

    @Test
    void getAllRoles_shouldReturnRolesFromDatabase() throws Exception {
        RoleEntity entity = new RoleEntity();
        entity.setName(RoleList.ROLE_ADMIN);
        entity.setDescription("Administrador");
        roleRepository.save(entity);

        mockMvc.perform(get("/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("ROLE_ADMIN"));
    }

    @Test
    void getRoleById_shouldReturnCorrectRole() throws Exception {
        RoleEntity entity = new RoleEntity();
        entity.setName(RoleList.ROLE_CUSTOMER);
        entity.setDescription("Cliente");
        RoleEntity role = roleRepository.save(entity);

        mockMvc.perform(get("/v1/roles/" + role.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ROLE_CUSTOMER"));
    }

    @Test
    void getRoleByName_shouldReturnCorrectRole() throws Exception {
        RoleEntity entity = new RoleEntity();
        entity.setName(RoleList.ROLE_CUSTOMER);
        entity.setDescription("Cliente");
        roleRepository.save(entity);

        mockMvc.perform(get("/v1/roles/name/" + "ROLE_CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Cliente"));
    }

    @Test
    void updateRole_shouldUpdateRole() throws Exception {
        RoleEntity oldRole = new RoleEntity();
        oldRole.setName(RoleList.ROLE_ADMIN);
        oldRole.setDescription("TO_UPDATE");
        RoleEntity role = roleRepository.save(oldRole);


        RoleRequestDto request = new RoleRequestDto();
        request.setName("ROLE_ADMIN");
        request.setDescription("Administrador");

        mockMvc.perform(put("/v1/roles/" + role.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Optional<RoleEntity> getRole = roleRepository.findById(role.getId());
        assertTrue(getRole.isPresent());
        assertEquals("Administrador", getRole.get().getDescription());
    }

    @Test
    void deleteRole_shouldRemoveRole() throws Exception {
        RoleEntity entity = new RoleEntity();
        entity.setName(RoleList.ROLE_ADMIN);
        entity.setDescription("TO_DELETE");
        RoleEntity role = roleRepository.save(entity);

        mockMvc.perform(delete("/v1/roles/" + role.getId()))
                .andExpect(status().isNoContent());

        assertTrue(roleRepository.findById(role.getId()).isEmpty());
    }

}