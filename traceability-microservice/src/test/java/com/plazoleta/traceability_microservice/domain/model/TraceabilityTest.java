package com.plazoleta.traceability_microservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TraceabilityTest {

    @Test
    void builder_shouldBuildObjectCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        Traceability traceability = Traceability.builder()
                .id("abc123")
                .orderId(1L)
                .customerId(2L)
                .customerEmail("customer@example.com")
                .date(now)
                .previousState("PENDING")
                .newState("READY")
                .employedId(3L)
                .employedEmail("employee@example.com")
                .build();

        assertEquals("abc123", traceability.getId());
        assertEquals(1L, traceability.getOrderId());
        assertEquals(2L, traceability.getCustomerId());
        assertEquals("customer@example.com", traceability.getCustomerEmail());
        assertEquals(now, traceability.getDate());
        assertEquals("PENDING", traceability.getPreviousState());
        assertEquals("READY", traceability.getNewState());
        assertEquals(3L, traceability.getEmployedId());
        assertEquals("employee@example.com", traceability.getEmployedEmail());
    }
}