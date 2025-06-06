package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Operaciones relacionadas con los platos")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class DishController {
    private final IDishHandler dishHandler;

    @Operation(summary = "Crear un nuevo plato para un restaurante",
            description = "Solo accesible para usuarios con rol OWNER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear platos"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    @PostMapping()
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<DishResponseDto> createDish(@Valid @RequestBody DishRequestDto dishRequestDto) {
        DishResponseDto createdDish = dishHandler.createDish(dishRequestDto);
        return new ResponseEntity<>(createdDish, HttpStatus.CREATED);

    }

    @Operation(summary = "Actualizar precio y descripción de un plato existente",
            description = "Solo accesible para usuarios con rol OWNER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plato actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para actualizar platos"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @PatchMapping("/{dishId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> updateDishPriceAndDescription(@PathVariable Long dishId,
                                           @Valid @RequestBody DishUpdateRequestDto dishUpdateRequestDto) {
        dishHandler.updateDishPriceAndDescription(dishId, dishUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar el estado activo de un plato",
            description = "Solo accesible para usuarios con rol OWNER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estado del plato actualizado correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado para cambiar el estado del plato"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @PatchMapping("/{dishId}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> changeDishStatus(@PathVariable Long dishId,
                                                 @RequestParam boolean activate) {
        dishHandler.changeDishStatus(dishId, activate);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Listar platos por restaurante y categoría (paginado) (Solo el rol de cliente)",
            description = "Este endpoint permite obtener una lista paginada de platos asociados a un restaurante específico y filtrados por el nombre de una categoría determinada. "
                    + "Los resultados se ordenan alfabéticamente por el nombre del plato.",
            parameters = {
                    @Parameter(name = "restaurantId", description = "ID del restaurante", required = true, example = "1"),
                    @Parameter(name = "categoryId", description = "ID de la categoría", required = true, example = "3"),
                    @Parameter(name = "page", description = "Número de página (comienza en 0)", example = "0"),
                    @Parameter(name = "size", description = "Cantidad de elementos por página", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de platos obtenida correctamente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DishResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "No se encontraron platos con los criterios especificados", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/restaurant/{restaurantId}/category/{categoryId}")
    public List<DishResponseDto> getDishesByCategory(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return dishHandler.getDishesByRestaurantAndCategory(restaurantId,categoryId, page, size);
    }
}