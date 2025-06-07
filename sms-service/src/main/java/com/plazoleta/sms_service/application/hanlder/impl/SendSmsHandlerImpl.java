package com.plazoleta.sms_service.application.hanlder.impl;

import com.plazoleta.sms_service.application.dto.request.SmsRequestDto;
import com.plazoleta.sms_service.application.hanlder.ISendSmsHandler;
import com.plazoleta.sms_service.application.mapper.ISmsRequestMapper;
import com.plazoleta.sms_service.domain.api.ISendSmsServicePort;
import com.plazoleta.sms_service.domain.model.SmsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendSmsHandlerImpl implements ISendSmsHandler {

    private final ISendSmsServicePort sendSmsPort;
    private final ISmsRequestMapper iSmsRequestMapper;

    @Override
    public void sendSms(SmsRequestDto smsRequestDto) {
        SmsMessage smsMessage = iSmsRequestMapper.toSmsMessage(smsRequestDto);
        sendSmsPort.sendSms(smsMessage);
    }
}
