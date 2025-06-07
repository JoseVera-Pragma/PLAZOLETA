package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.plazoleta.plazoleta_microservice.application.dto.request.CreateOrderRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.OrderResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IOrderHandler;
import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operaciones relacionadas con pedidos")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class OrderController {

    private final IOrderHandler orderHandler;

    @Operation(
            summary = "Crear un nuevo pedido",
            description = "Este endpoint permite a un usuario con rol CUSTOMER crear un nuevo pedido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - JWT inválido o no proporcionado"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene rol CUSTOMER"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderRequestDto createOrderRequestDto) {
        orderHandler.createOrder(createOrderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Obtener pedidos por estado",
            description = "Este endpoint permite a los empleados consultar los pedidos de un restaurante específico, filtrados por su estado actual. La respuesta está paginada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de pedidos devuelta exitosamente",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = OrderResponseDto.class)))),
                    @ApiResponse(responseCode = "404", description = "No se encontraron datos")})
    @PreAuthorize("hasRole('EMPLOYED')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status,

                                                                    @Parameter(description = "Número de página (inicia en 0)", example = "0")
                                                                    @RequestParam(defaultValue = "0") @Min(0) int page,

                                                                    @Parameter(description = "Cantidad de elementos por página", example = "10")
                                                                    @RequestParam(defaultValue = "10") @Min(1) int size) {
        return ResponseEntity.ok(orderHandler.findOrdersByStatusForAuthenticatedEmployee(status, page, size));
    }

    @Operation(summary = "Asignar una orden a un empleado",
            description = "Asigna la orden al empleado autenticado y cambia el estado a 'En preparación'",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Orden asignada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Orden no encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"))),
                    @ApiResponse(responseCode = "403", description = "No autorizado para esta operación")
            })
    @PreAuthorize("hasRole('EMPLOYED')")
    @PatchMapping("/assign/{orderId}")
    public ResponseEntity<Void> assignOrderToEmployed(@PathVariable Long orderId) {
        orderHandler.assignOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Marcar pedido como listo",
            description = "Marca el pedido como 'READY', genera un PIN de seguridad y envía un SMS al cliente notificando que su pedido está listo."
    )
    @PreAuthorize("hasRole('EMPLOYED')")
    @PostMapping("/{orderId}/ready")
    public ResponseEntity<Void> markOrderAsReady(
            @Parameter(description = "ID del pedido a marcar como listo", example = "123")
            @PathVariable Long orderId
    ) {
        orderHandler.markOrderAsReady(orderId);
        return ResponseEntity.ok().build();
    }
}
