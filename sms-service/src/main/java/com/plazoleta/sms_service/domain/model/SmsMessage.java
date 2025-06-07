package com.plazoleta.sms_service.domain.model;

public class SmsMessage {
    private final String phoneNumber;
    private final String message;

    public SmsMessage(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
