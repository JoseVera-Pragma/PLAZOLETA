package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurantes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre",nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nit;

    @Column(name = "direccion", nullable = false)
    private String address;

    @Column(name = "telefono", nullable = false, length = 13)
    private String phoneNumber;

    @Column(name = "url_logo", nullable = false)
    private String urlLogo;

    @Column(name = "id_propietario", nullable = false)
    private Long idOwner;
}
