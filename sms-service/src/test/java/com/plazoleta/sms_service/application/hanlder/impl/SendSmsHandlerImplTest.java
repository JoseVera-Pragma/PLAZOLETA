package com.plazoleta.sms_service.application.hanlder.impl;

import com.plazoleta.sms_service.application.dto.request.SmsRequestDto;
import com.plazoleta.sms_service.domain.api.ISendSmsServicePort;
import com.plazoleta.sms_service.domain.model.SmsMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SendSmsHandlerImplTest {

    private ISendSmsServicePort sendSmsPort;
    private SendSmsHandlerImpl sendSmsHandler;

    @BeforeEach
    void setUp() {
        sendSmsPort = mock(ISendSmsServicePort.class);
        sendSmsHandler = new SendSmsHandlerImpl(sendSmsPort);
    }

    @Test
    void sendSms_ShouldMapAndDelegateToPort() {
        SmsRequestDto dto = new SmsRequestDto("+123456789", "Hello!");
        SmsMessage expectedMessage = new SmsMessage("+123456789", "Hello!");

        sendSmsHandler.sendSms(dto);

        ArgumentCaptor<SmsMessage> captor = ArgumentCaptor.forClass(SmsMessage.class);
        verify(sendSmsPort).sendSms(captor.capture());

        SmsMessage sentMessage = captor.getValue();
        assertEquals("+123456789", sentMessage.getPhoneNumber());
        assertEquals("Hello!", sentMessage.getMessage());
    }
}