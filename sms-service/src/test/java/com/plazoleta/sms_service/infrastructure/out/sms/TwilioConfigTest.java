package com.plazoleta.sms_service.infrastructure.out.sms;

import com.twilio.Twilio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class TwilioConfigTest {
    private TwilioConfig twilioConfig;

    @BeforeEach
    void setUp() {
        twilioConfig = new TwilioConfig();

        ReflectionTestUtils.setField(twilioConfig, "accountSid", "test-sid");
        ReflectionTestUtils.setField(twilioConfig, "authToken", "test-token");
        ReflectionTestUtils.setField(twilioConfig, "fromPhoneNumber", "+123456789");
    }

    @Test
    void init_shouldCallTwilioInit() {
        try (MockedStatic<Twilio> mockedTwilio = mockStatic(Twilio.class)) {
            twilioConfig.init();

            mockedTwilio.verify(() -> Twilio.init("test-sid", "test-token"), times(1));
        }
    }

}