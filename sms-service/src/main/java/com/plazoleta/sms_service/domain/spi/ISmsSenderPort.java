package com.plazoleta.sms_service.domain.spi;

import com.plazoleta.sms_service.domain.model.SmsMessage;

public interface ISmsSenderPort {
    void sendSms(SmsMessage smsMessage);
}
