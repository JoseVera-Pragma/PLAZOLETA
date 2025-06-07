package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.domain.spi.ISendSmsPort;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.dto.SmsRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsSenderAdapter implements ISendSmsPort {

    private final SmsFeignClient smsFeignClient;

    @Override
    public void sendSms(String phoneNumber, String message) {
        SmsRequestDto dto = new SmsRequestDto(phoneNumber, message);
        smsFeignClient.sendSms(dto);
    }
}
