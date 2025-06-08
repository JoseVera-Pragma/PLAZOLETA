package com.plazoleta.sms_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"JWT_SECRET=dummy-secret",
		"TWILIO_ACCOUNT_SID=dummy-sid",
		"TWILIO_AUTH_TOKEN=dummy-token",
		"TWILIO_PHONE_NUMBER=+1234567890"
})
class SmsServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
