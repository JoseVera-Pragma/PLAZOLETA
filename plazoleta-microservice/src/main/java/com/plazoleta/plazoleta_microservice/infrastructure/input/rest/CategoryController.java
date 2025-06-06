package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.plazoleta.plazoleta_microservice.application.dto.request.CategoryRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.CategoryResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.ICategoryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Operaciones para gestionar las categorías de los platos solo accesible para usuarios con roles ADMIN y OWNER.")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class CategoryController {
    private final ICategoryHandler categoryHandler;

    @Operation(summary = "Crear una nueva categoría", description = "Crea una nueva categoría de plato. Requiere rol ADMIN o propietario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear categorías", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @Valid @RequestBody CategoryRequestDto categoryRequestDto
    ) {
        CategoryResponseDto response = categoryHandler.createCategory(categoryRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todas las categorías", description = "Obtiene la lista de todas las categorías disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado para ver categorías", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryHandler.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Eliminar una categoría", description = "Elimina una categoría por su ID. Requiere rol ADMIN o propietario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado para eliminar categorías", content = @Content)
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long categoryId) {
        categoryHandler.deleteCategoryById(categoryId);
        return ResponseEntity.noContent().build();
    }
}
