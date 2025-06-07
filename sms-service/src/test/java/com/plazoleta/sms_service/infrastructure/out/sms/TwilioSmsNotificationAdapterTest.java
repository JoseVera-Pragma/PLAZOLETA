package com.plazoleta.sms_service.infrastructure.out.sms;

import com.plazoleta.sms_service.domain.model.SmsMessage;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class TwilioSmsNotificationAdapterTest {

    private TwilioSmsNotificationAdapter adapter;
    private TwilioConfig config;

    @BeforeEach
    void setUp() {
        config = mock(TwilioConfig.class);
        when(config.getFromPhoneNumber()).thenReturn("+15556667777");
        adapter = new TwilioSmsNotificationAdapter(config);
    }

    @Test
    void sendSms_shouldCreateAndSendMessageUsingTwilio() {
        SmsMessage smsMessage = new SmsMessage("+1234567890", "Test SMS");
        MessageCreator creatorMock = mock(MessageCreator.class);
        Message messageMock = mock(Message.class);
        when(messageMock.getSid()).thenReturn("SM123456");

        try (MockedStatic<Message> messageStaticMock = mockStatic(Message.class)) {
            messageStaticMock.when(() ->
                    Message.creator(
                            new PhoneNumber(smsMessage.getPhoneNumber()),
                            new PhoneNumber(config.getFromPhoneNumber()),
                            smsMessage.getMessage()
                    )
            ).thenReturn(creatorMock);

            when(creatorMock.create()).thenReturn(messageMock);

            adapter.sendSms(smsMessage);

            messageStaticMock.verify(() ->
                    Message.creator(
                            new PhoneNumber(smsMessage.getPhoneNumber()),
                            new PhoneNumber(config.getFromPhoneNumber()),
                            smsMessage.getMessage()
                    ), times(1)
            );
            verify(creatorMock, times(1)).create();
        }
    }
}