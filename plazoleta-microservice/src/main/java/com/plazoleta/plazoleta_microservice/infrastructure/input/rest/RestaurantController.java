package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Operaciones relacionadas con restaurantes y sus platos")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantController {
    private final IRestaurantHandler restaurantHandler;
    private final IDishHandler dishHandler;

    @Operation(summary = "Crear un nuevo restaurante",
            description = "Solo accesible para usuarios con rol ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear restaurantes")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        restaurantHandler.createRestaurant(restaurantRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Crear un nuevo plato para un restaurante",
            description = "Solo accesible para usuarios con rol OWNER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear platos"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @PostMapping("/{restaurantId}/dishes")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<DishResponseDto> createDish(@PathVariable Long restaurantId,
                                                      @Valid @RequestBody DishRequestDto dishRequestDto) {
        DishResponseDto createdDish = dishHandler.createDish(restaurantId, dishRequestDto);
        return new ResponseEntity<>(createdDish, HttpStatus.CREATED);

    }

    @Operation(summary = "Actualizar un plato existente",
            description = "Solo accesible para usuarios con rol OWNER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plato actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para actualizar platos"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @PutMapping("/{restaurantId}/dishes/{dishId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> updateDish(@PathVariable Long restaurantId,
                                           @PathVariable Long dishId,
                                           @Valid @RequestBody DishUpdateRequestDto dishUpdateRequestDto) {
        dishHandler.updateDish(dishId, dishUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar el estado activo de un plato",
            description = "Solo accesible para usuarios con rol OWNER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estado del plato actualizado correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado para cambiar el estado del plato"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @PatchMapping("/dishes/{dishId}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> changeDishStatus(@PathVariable Long dishId,
                                                 @RequestParam boolean activate) {
        dishHandler.changeDishStatus(dishId, activate);
        return ResponseEntity.noContent().build();
    }
}
