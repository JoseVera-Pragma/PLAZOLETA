package com.plazoleta.traceability_microservice.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class EfficiencyReportTest {

    @Test
    void testEfficiencyReportConstructorAndGetters() {
        Long orderId = 1L;
        Duration duration = Duration.ofMinutes(45);
        Long employeeId = 10L;
        String employeeEmail = "employee@example.com";

        EfficiencyReport report = new EfficiencyReport(orderId, duration, employeeId, employeeEmail);

        assertEquals(orderId, report.getOrderId());
        assertEquals(duration, report.getDuration());
        assertEquals(employeeId, report.getEmployeeId());
        assertEquals(employeeEmail, report.getEmployeeEmail());
    }

    @Test
    void testEfficiencyReportSetters() {
        EfficiencyReport report = new EfficiencyReport(null, null, null, null);

        Long newOrderId = 2L;
        Duration newDuration = Duration.ofHours(1);
        Long newEmployeeId = 20L;
        String newEmail = "new@example.com";

        report.setOrderId(newOrderId);
        report.setDuration(newDuration);
        report.setEmployeeId(newEmployeeId);
        report.setEmployeeEmail(newEmail);

        assertEquals(newOrderId, report.getOrderId());
        assertEquals(newDuration, report.getDuration());
        assertEquals(newEmployeeId, report.getEmployeeId());
        assertEquals(newEmail, report.getEmployeeEmail());
    }
}