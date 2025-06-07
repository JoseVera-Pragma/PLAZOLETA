package com.plazoleta.plazoleta_microservice.domain.spi;

public interface ISendSmsPort  {
    void sendSms(String phoneNumber, String message);
}
