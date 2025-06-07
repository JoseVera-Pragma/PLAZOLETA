package com.plazoleta.sms_service.domain.usecase;

import com.plazoleta.sms_service.domain.model.SmsMessage;
import com.plazoleta.sms_service.domain.spi.ISmsSenderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SmsMessageUseCaseTest {

    private ISmsSenderPort smsSenderPort;
    private SmsMessageUseCase smsMessageUseCase;

    @BeforeEach
    void setUp() {
        smsSenderPort = mock(ISmsSenderPort.class);
        smsMessageUseCase = new SmsMessageUseCase(smsSenderPort);
    }

    @Test
    void testSendSms_DelegatesToSenderPort() {
        SmsMessage message = new SmsMessage("+123456789", "Hello!");

        smsMessageUseCase.sendSms(message);

        verify(smsSenderPort, times(1)).sendSms(message);
    }

}