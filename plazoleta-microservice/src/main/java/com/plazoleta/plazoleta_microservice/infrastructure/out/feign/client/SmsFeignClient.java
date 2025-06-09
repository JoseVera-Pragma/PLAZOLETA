package com.plazoleta.plazoleta_microservice.infrastructure.out.feign.client;

import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.FeignConfig;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feign.dto.SmsRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sms-microservice", url = "${sms.service.url}",
        configuration = FeignConfig.class)
public interface SmsFeignClient {

    @PostMapping("/sms")
    void sendSms (@RequestBody SmsRequestDto smsRequestDto);
}
