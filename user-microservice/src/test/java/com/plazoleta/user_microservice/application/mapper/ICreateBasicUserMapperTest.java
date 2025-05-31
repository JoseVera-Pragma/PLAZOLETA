package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.application.dto.request.CreateBasicUserRequestDto;
import com.plazoleta.user_microservice.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ICreateBasicUserMapperTest {

    @Autowired
    private ICreateBasicUserMapper mapper;

    @Test
    void toUser_shouldMapUserRequestDtoToUserCorrectly() {
        CreateBasicUserRequestDto dto = new CreateBasicUserRequestDto();
        dto.setFirstName("Jose");
        dto.setLastName("Vera");
        dto.setEmail("jose@correo.com");
        dto.setPhoneNumber("3216549870");
        dto.setIdentityNumber("1093854586");
        dto.setPassword("supersegura");

        User user = mapper.toUser(dto);

        assertNotNull(user);
        assertEquals("Jose", user.getFirstName());
        assertEquals("Vera", user.getLastName());
        assertEquals("jose@correo.com", user.getEmail().getValue());
        assertEquals("3216549870", user.getPhoneNumber().getValue());
        assertEquals("1093854586", user.getIdentityNumber().getValue());
        assertEquals("supersegura", user.getPassword());
   }

}