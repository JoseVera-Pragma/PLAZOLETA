package com.plazoleta.user_microservice.infrastructure.out.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String firstName;

    @Column(name = "apellido", nullable = false)
    private String lastName;

    @Column(name = "numero_documento", nullable = false)
    private String identityNumber;

    @Column(name = "celular", nullable = false)
    private String phoneNumber;

    @Column(name = "fecha_nacimiento")
    private LocalDate dateOfBirth;

    @Column(name = "correo", unique = true, nullable = false)
    private String email;

    @Column(name = "clave", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @Column(name = "restaurante_id")
    private Long restaurantId;
}
