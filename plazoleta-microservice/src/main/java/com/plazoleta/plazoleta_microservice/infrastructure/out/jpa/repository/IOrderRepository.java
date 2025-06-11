package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository;

import com.plazoleta.plazoleta_microservice.domain.model.OrderStatus;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndStatusIn(Long customerId, List<OrderStatus> statuses);

    Page<OrderEntity> findAllByRestaurantIdAndStatus(Long restaurantId, OrderStatus status, Pageable pageable);
}