package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.domain.exception.UserNotFoundException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FeignErrorDecoderTest {

    private final FeignErrorDecoder decoder = new FeignErrorDecoder();

    @Test
    void shouldThrowUserNotFoundExceptionFor404() {
        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(Request.create(Request.HttpMethod.GET, "/users/8", Collections.emptyMap(), null, StandardCharsets.UTF_8, null))
                .build();

        Exception exception = decoder.decode("UserClient#getUserById", response);
        assertInstanceOf(UserNotFoundException.class, exception);
        assertEquals("User not found in remote service", exception.getMessage());
    }

    @Test
    void shouldDelegateToDefaultDecoderForOtherStatusCodes() {
        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(Request.create(Request.HttpMethod.GET, "/users/j", Collections.emptyMap(), null, StandardCharsets.UTF_8, null))
                .build();

        Exception exception = decoder.decode("UserClient#getUserById", response);

        assertNotNull(exception);
        assertFalse(exception instanceof UserNotFoundException);
    }
}