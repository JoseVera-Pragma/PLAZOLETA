package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResponseDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.RestaurantResumeResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
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
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Operaciones relacionadas con restaurantes y sus platos")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class RestaurantController {
    private final IRestaurantHandler restaurantHandler;

    @Operation(summary = "Crear un nuevo restaurante",
            description = "Solo accesible para usuarios con rol ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear restaurantes")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponseDto> createRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        RestaurantResponseDto restaurantResponseDto = restaurantHandler.createRestaurant(restaurantRequestDto);
        return new ResponseEntity<>(restaurantResponseDto,HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar restaurantes paginados y ordenados alfabéticamente (Solo el rol de cliente)",
            description = "Devuelve una lista paginada de restaurantes con su nombre y logo, ordenados por nombre ascendente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public List<RestaurantResumeResponseDto> getRestaurants(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Cantidad de elementos por página", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return restaurantHandler.restaurantList(page, size);
    }
}
