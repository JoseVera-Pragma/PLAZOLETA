package com.plazoleta.user_microservice.application.mapper;

import com.plazoleta.user_microservice.domain.model.Email;
import com.plazoleta.user_microservice.domain.model.IdentityNumber;
import com.plazoleta.user_microservice.domain.model.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueObjectMapperTest {
    private ValueObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ValueObjectMapper() {};
    }

    @Test
    void testToEmail() {
        String emailStr = "jose@example.com";
        Email email = mapper.toEmail(emailStr);
        assertNotNull(email);
        assertEquals(emailStr, email.getValue());
    }

    @Test
    void testFromEmail() {
        Email email = new Email("jose@example.com");
        String result = mapper.fromEmail(email);
        assertEquals("jose@example.com", result);
    }

    @Test
    void testToIdentityNumber() {
        String id = "1093854586";
        IdentityNumber identityNumber = mapper.toIdentityNumber(id);
        assertNotNull(identityNumber);
        assertEquals(id, identityNumber.getValue());
    }

    @Test
    void testFromIdentityNumber() {
        IdentityNumber identityNumber = new IdentityNumber("1093854586");
        String result = mapper.fromIdentityNumber(identityNumber);
        assertEquals("1093854586", result);
    }

    @Test
    void testToPhoneNumber() {
        String phone = "3216549870";
        PhoneNumber phoneNumber = mapper.toPhoneNumber(phone);
        assertNotNull(phoneNumber);
        assertEquals(phone, phoneNumber.getValue());
    }

    @Test
    void testFromPhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber("3216549870");
        String result = mapper.fromPhoneNumber(phoneNumber);
        assertEquals("3216549870", result);
    }
}