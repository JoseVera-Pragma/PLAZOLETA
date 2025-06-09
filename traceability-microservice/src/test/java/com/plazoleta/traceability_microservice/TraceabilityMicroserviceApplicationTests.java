package com.plazoleta.traceability_microservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=test-secret")
class TraceabilityMicroserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
