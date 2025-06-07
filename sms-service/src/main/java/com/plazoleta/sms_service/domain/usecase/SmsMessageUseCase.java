package com.plazoleta.sms_service.domain.usecase;

import com.plazoleta.sms_service.domain.api.ISendSmsServicePort;
import com.plazoleta.sms_service.domain.model.SmsMessage;
import com.plazoleta.sms_service.domain.spi.ISmsSenderPort;

public class SmsMessageUseCase implements ISendSmsServicePort {
    private final ISmsSenderPort smsSenderPort;

    public SmsMessageUseCase(ISmsSenderPort smsSenderPort) {
        this.smsSenderPort = smsSenderPort;
    }

    @Override
    public void sendSms(SmsMessage smsMessage) {
        smsSenderPort.sendSms(smsMessage);
    }
}