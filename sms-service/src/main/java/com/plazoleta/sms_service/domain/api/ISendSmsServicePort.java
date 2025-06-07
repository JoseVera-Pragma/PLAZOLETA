package com.plazoleta.sms_service.domain.api;

import com.plazoleta.sms_service.domain.model.SmsMessage;

public interface ISendSmsServicePort {
    void sendSms(SmsMessage smsMessage);
}
