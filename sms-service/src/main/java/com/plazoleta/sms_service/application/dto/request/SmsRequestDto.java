package com.plazoleta.sms_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsRequestDto {

    @NotBlank(message = "Phone number is required")
    @Size(max = 13, message = "Phone number is extensive max 13 characters")
    @Pattern(regexp = "^\\+?\\d+$", message = "Phone number is not valid")
    private String phoneNumber;

    @NotBlank(message = "Message content is required")
    private String message;
}
