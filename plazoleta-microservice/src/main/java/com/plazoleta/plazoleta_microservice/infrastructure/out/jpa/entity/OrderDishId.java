package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDishId implements Serializable {

    @Column(name = "id_pedido")
    private Long orderId;

    @Column(name = "id_plato")
    private Long dishId;
}
