package com.plazoleta.sms_service.application.hanlder.impl;

import com.plazoleta.sms_service.application.dto.request.SmsRequestDto;
import com.plazoleta.sms_service.application.mapper.ISmsRequestMapper;
import com.plazoleta.sms_service.domain.api.ISendSmsServicePort;
import com.plazoleta.sms_service.domain.model.SmsMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SendSmsHandlerImplTest {

    private ISendSmsServicePort sendSmsPort;
    private ISmsRequestMapper smsRequestMapper;
    private SendSmsHandlerImpl sendSmsHandler;

    @BeforeEach
    void setUp() {
        sendSmsPort = mock(ISendSmsServicePort.class);
        smsRequestMapper = mock(ISmsRequestMapper.class);
        sendSmsHandler = new SendSmsHandlerImpl(sendSmsPort, smsRequestMapper);
    }

    @Test
    void sendSms_ShouldMapAndDelegateToPort() {
        SmsRequestDto dto = new SmsRequestDto("+123456789", "Hello!");
        SmsMessage expectedMessage = new SmsMessage("+123456789", "Hello!");

        when(smsRequestMapper.toSmsMessage(dto)).thenReturn(expectedMessage);

        sendSmsHandler.sendSms(dto);

        verify(smsRequestMapper, times(1)).toSmsMessage(dto);
        verify(sendSmsPort, times(1)).sendSms(expectedMessage);
    }
}