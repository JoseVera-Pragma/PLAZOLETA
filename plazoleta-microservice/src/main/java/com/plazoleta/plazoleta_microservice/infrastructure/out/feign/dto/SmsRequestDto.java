package com.plazoleta.plazoleta_microservice.infrastructure.out.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsRequestDto {
    private String phoneNumber;
    private String message;
}