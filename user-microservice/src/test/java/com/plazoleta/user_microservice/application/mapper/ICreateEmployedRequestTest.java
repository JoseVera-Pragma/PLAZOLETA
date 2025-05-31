package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ICreateEmployedRequestTest {

    @Autowired
    private ICreateEmployedRequest mapper;

    @Test
    void shouldMapDtoToUserCorrectly() {
        CreateEmployedRequestDto dto = new CreateEmployedRequestDto();
        dto.setFirstName("Carlos");
        dto.setLastName("Ramírez");
        dto.setEmail("carlos@correo.com");
        dto.setPhoneNumber("3001234567");
        dto.setIdentityNumber("12345678");
        dto.setPassword("secure123");

        User user = mapper.toUser(dto);

        assertNotNull(user);
        assertEquals("Carlos", user.getFirstName());
        assertEquals("Ramírez", user.getLastName());
        assertEquals("carlos@correo.com", user.getEmail().getValue());
        assertEquals("3001234567", user.getPhoneNumber().getValue());
        assertEquals("12345678", user.getIdentityNumber().getValue());
        assertEquals("secure123", user.getPassword());
    }
}