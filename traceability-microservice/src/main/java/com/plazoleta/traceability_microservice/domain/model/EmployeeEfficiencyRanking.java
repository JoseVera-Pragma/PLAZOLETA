package com.plazoleta.traceability_microservice.domain.model;

import java.time.Duration;

public class EmployeeEfficiencyRanking {
    private final Long employeeId;
    private final String employeeEmail;
    private final Duration duration;

    public EmployeeEfficiencyRanking(Long employeeId, String employeeEmail, Duration duration) {
        this.employeeId = employeeId;
        this.employeeEmail = employeeEmail;
        this.duration = duration;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public Duration getDuration() {
        return duration;
    }
}
