package com.plazoleta.user_microservice.infrastructure.input.rest;

import com.plazoleta.user_microservice.application.dto.request.RoleRequestDto;
import com.plazoleta.user_microservice.application.dto.response.RoleResponseDto;
import com.plazoleta.user_microservice.application.handler.IRoleHandler;
import com.plazoleta.user_microservice.domain.model.RoleList;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(value = "/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Operaciones relacionadas con la gestión de roles de usuario.")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final IRoleHandler roleHandler;

    @Operation(summary = "Crear un nuevo rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para crear roles")
    })
    @PostMapping
    public ResponseEntity<Void> createRole(@Valid @RequestBody RoleRequestDto roleRequestDto) {
        roleHandler.createRole(roleRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Obtener todos los roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado para consultar roles")
    })
    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        return ResponseEntity.ok(roleHandler.getAllRoles());
    }

    @Operation(summary = "Obtener rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para consultar roles")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleHandler.getRoleById(id));
    }

    @Operation(summary = "Obtener rol por nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para consultar roles")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<RoleResponseDto> getRoleByName(@PathVariable RoleList name) {
        return ResponseEntity.ok(roleHandler.getRoleByName(name));
    }

    @Operation(summary = "Actualizar un rol existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado para actualizar roles"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRole(@PathVariable Long id,
                                           @Valid @RequestBody RoleRequestDto roleRequestDto) {
        roleHandler.updateRole(id, roleRequestDto);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Eliminar un rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado para eliminar roles"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleHandler.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

}
