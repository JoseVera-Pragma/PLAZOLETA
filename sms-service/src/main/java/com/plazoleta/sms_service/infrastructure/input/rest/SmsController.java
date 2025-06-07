package com.plazoleta.sms_service.infrastructure.input.rest;


import com.plazoleta.sms_service.application.dto.request.SmsRequestDto;
import com.plazoleta.sms_service.application.hanlder.ISendSmsHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@Tag(name = "SMS", description = "Operations related to sending SMS messages")
@SecurityRequirement(name = "bearerAuth")
public class SmsController {

    private final ISendSmsHandler sendSmsHandler;

    @Operation(
            summary = "Send SMS",
            description = "Sends an SMS message to a specified phone number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SMS sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> send(@RequestBody @Valid SmsRequestDto dto) {
        sendSmsHandler.sendSms(dto);
        return ResponseEntity.ok().build();
    }
}
