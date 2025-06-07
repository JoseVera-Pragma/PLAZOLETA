package com.plazoleta.sms_service.infrastructure.out.sms;

import com.plazoleta.sms_service.domain.model.SmsMessage;
import com.plazoleta.sms_service.domain.spi.ISmsSenderPort;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TwilioSmsNotificationAdapter implements ISmsSenderPort {

    private final TwilioConfig twilioConfig;

    @Override
    public void sendSms(SmsMessage smsMessage) {
        Message message = Message.creator(
                    new PhoneNumber(smsMessage.getPhoneNumber()),
                    new PhoneNumber(twilioConfig.getFromPhoneNumber()),
                    smsMessage.getMessage()
                ).create();
        log.info("Message sent! SID: "+message.getSid());
    }
}
