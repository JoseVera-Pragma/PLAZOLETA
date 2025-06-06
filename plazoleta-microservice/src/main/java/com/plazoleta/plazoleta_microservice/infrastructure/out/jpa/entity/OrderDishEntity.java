package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedidos_platos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDishEntity {

    @EmbeddedId
    private OrderDishId id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "id_pedido")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dishId")
    @JoinColumn(name = "id_plato")
    private DishEntity dish;

    @Column(name = "cantidad", nullable = false)
    private Integer quantity;

}