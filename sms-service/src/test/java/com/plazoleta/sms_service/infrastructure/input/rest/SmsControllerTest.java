package com.plazoleta.sms_service.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.sms_service.application.dto.request.SmsRequestDto;
import com.plazoleta.sms_service.application.hanlder.ISendSmsHandler;
import com.plazoleta.sms_service.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SmsController.class)
@AutoConfigureMockMvc(addFilters = false)
class SmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ISendSmsHandler sendSmsHandler;

    @MockitoBean
    private JwtTokenAdapter jwtTokenAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<SmsRequestDto> smsRequestCaptor;

    @Test
    void send_shouldReturnOk_whenValidRequest() throws Exception {
        SmsRequestDto request = new SmsRequestDto("+1234567890", "Hello!");

        mockMvc.perform(post("/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(sendSmsHandler).sendSms(smsRequestCaptor.capture());

        SmsRequestDto captured = smsRequestCaptor.getValue();
        assertEquals(request.getPhoneNumber(), captured.getPhoneNumber());
        assertEquals(request.getMessage(), captured.getMessage());
    }

    @Test
    void send_shouldReturnBadRequest_whenInvalidInput() throws Exception {
        SmsRequestDto invalidDto = new SmsRequestDto(null, "");

        mockMvc.perform(post("/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}