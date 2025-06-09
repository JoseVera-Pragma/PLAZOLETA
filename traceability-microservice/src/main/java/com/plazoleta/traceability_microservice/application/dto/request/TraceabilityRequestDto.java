package com.plazoleta.traceability_microservice.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TraceabilityRequestDto {

    @NotNull(message = "Order ID must not be null")
    private Long orderId;

    @NotNull(message = "Customer ID must not be null")
    private Long customerId;

    @NotBlank(message = "Customer email must not be blank")
    @Email(message = "Customer email must be a valid email address")
    private String customerEmail;

    @Pattern(
            regexp = "^\\S(.*\\S)?$",
            message = "Previous state must not be blank or only whitespace if present"
    )
    private String previousState;

    @NotBlank(message = "New state must not be blank")
    private String newState;

    private Long employedId;

    @Pattern(regexp = "^\\S(.*\\S)?$", message = "Employee email must not be blank or only whitespace")
    @Email(message = "Employee email must be a valid email address")
    private String employedEmail;
}