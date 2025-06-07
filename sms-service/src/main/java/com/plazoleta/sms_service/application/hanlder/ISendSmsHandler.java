package com.plazoleta.sms_service.application.hanlder;

import com.plazoleta.sms_service.application.dto.request.SmsRequestDto;

public interface ISendSmsHandler {
    void sendSms(SmsRequestDto smsRequestDto);
}
