package com.plazoleta.traceability_microservice.infrastructure.input.rest;

import com.plazoleta.traceability_microservice.application.dto.request.TraceabilityRequestDto;
import com.plazoleta.traceability_microservice.application.dto.response.TraceabilityResponseDto;
import com.plazoleta.traceability_microservice.application.handler.ITraceabilityHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/traceability")
@RequiredArgsConstructor
@Tag(name = "Trazabilidad", description = "API para registrar y consultar eventos de trazabilidad de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class TraceabilityController {

    private final ITraceabilityHandler handler;

    @Operation(summary = "Registrar un nuevo evento de trazabilidad de un pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento de trazabilidad registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud malformada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<Void> saveTraceability(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del evento de trazabilidad",
                    required = true
            )
            @Valid @RequestBody TraceabilityRequestDto requestDTO) {
        handler.saveTraceability(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Obtener historial de trazabilidad de un pedido para un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido o cliente no encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<List<TraceabilityResponseDto>> getTraceability(
            @Parameter(description = "ID del pedido", example = "123")
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(handler.getTraceabilityByOrder(orderId));
    }
}
