package com.plazoleta.user_microservice.infrastructure.out.jpa.entity;

import com.plazoleta.user_microservice.domain.model.RoleList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre",nullable = false, unique = true)
    private RoleList name;

    @Column(name = "descripcion")
    private String description;
}
