package com.plazoleta.traceability_microservice.domain.model;

import java.time.Duration;

public class EfficiencyReport {
    private Long orderId;
    private Duration duration;
    private Long employeeId;
    private String employeeEmail;

    public EfficiencyReport(Long orderId, Duration duration, Long employeeId, String employeeEmail) {
        this.orderId = orderId;
        this.duration = duration;
        this.employeeId = employeeId;
        this.employeeEmail = employeeEmail;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
