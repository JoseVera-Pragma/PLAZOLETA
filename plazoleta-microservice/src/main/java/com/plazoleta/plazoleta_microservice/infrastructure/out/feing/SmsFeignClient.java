package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.dto.SmsRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sms-microservice", url = "${sms.service.url}",
        configuration = FeignConfig.class)
public interface SmsFeignClient {

    @PostMapping("/sms")
    void sendSms (@RequestBody SmsRequestDto smsRequestDto);
}
