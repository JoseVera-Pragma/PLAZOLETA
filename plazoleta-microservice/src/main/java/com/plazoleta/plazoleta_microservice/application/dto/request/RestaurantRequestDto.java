package com.plazoleta.plazoleta_microservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RestaurantRequestDto {

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^(?!\\d+$)[\\w\\s]+$", message = "Name cannot be only numbers")
    private String name;

    @NotBlank(message = "NIT is required")
    @Pattern(regexp = "\\d+", message = "NIT must be numeric")
    private String nit;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?\\d{1,13}$", message = "Phone must be numeric and up to 13 digits, can start with +")
    private String phoneNumber;

    private String urlLogo;

    @NotNull(message = "Owner user ID is required")
    private Long idOwner;
}
