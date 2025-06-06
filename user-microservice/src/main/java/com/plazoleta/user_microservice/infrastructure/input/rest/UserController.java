package com.plazoleta.user_microservice.infrastructure.input.rest;

import com.plazoleta.user_microservice.application.dto.request.CreateEmployedRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateOwnerRequestDto;
import com.plazoleta.user_microservice.application.dto.request.CreateUserRequestDto;
import com.plazoleta.user_microservice.application.dto.response.UserResponseDto;
import com.plazoleta.user_microservice.application.handler.IUserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Operaciones para gestión de usuarios")
public class UserController {

    private final IUserHandler iUserHandler;

    @Operation(summary = "Crear un usuario cliente (registro público)", description = "Crea un usuario con rol CLIENTE")
    @ApiResponse(responseCode = "201", description = "Usuario cliente creado correctamente")
    @PostMapping("/customers")
    public ResponseEntity<Void> createCustomer(@Valid @RequestBody CreateUserRequestDto createCustomerRequestDto) {
        iUserHandler.createCustomer(createCustomerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Crear un usuario empleado", description = "Crea un usuario con rol EMPLOYED. Solo accesible para propietarios")
    @ApiResponse(responseCode = "201", description = "Usuario empleado creado correctamente")
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/employeds")
    public ResponseEntity<Void> createEmployed(@Valid @RequestBody CreateEmployedRequestDto createEmployedRequestDto) {
        iUserHandler.createEmployed(createEmployedRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Crear un usuario propietario", description = "Crea un usuario con rol OWNER")
    @ApiResponse(responseCode = "201", description = "Usuario propietario creado correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/owners")
    public ResponseEntity<Void> createOwner(@Valid @RequestBody CreateOwnerRequestDto createOwnerRequestDto) {
        iUserHandler.createOwner(createOwnerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Listado completo de usuarios, solo accesible para administradores")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = iUserHandler.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario por su identificador único")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = iUserHandler.getUser(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Obtener usuario por correo electrónico", description = "Obtiene un usuario a partir de su email")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        UserResponseDto user = iUserHandler.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Actualizar usuario por ID", description = "Actualiza datos del usuario. Accesible para ADMIN y OWNER")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
                                           @Valid @RequestBody CreateOwnerRequestDto createOwnerRequestDto) {
        iUserHandler.updateUser(id, createOwnerRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Eliminar usuario por ID", description = "Elimina un usuario de la base de datos")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        iUserHandler.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
