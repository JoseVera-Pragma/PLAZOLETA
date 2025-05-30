package com.plazoleta.plazoleta_microservice.infrastructure.input.rest;

import com.plazoleta.plazoleta_microservice.application.dto.request.DishRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.DishUpdateRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.request.RestaurantRequestDto;
import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final IRestaurantHandler restaurantHandler;
    private final IDishHandler dishHandler;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> createRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        restaurantHandler.createRestaurant(restaurantRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{restaurantId}/dishes")
    public ResponseEntity<DishResponseDto> createDish(@PathVariable Long restaurantId,
                                                      @Valid @RequestBody DishRequestDto dishRequestDto) {
        DishResponseDto createdDish = dishHandler.createDish(restaurantId, dishRequestDto);
        return new ResponseEntity<>(createdDish, HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{restaurantId}/dishes/{dishId}")
    public ResponseEntity<Void> updateDish(@PathVariable Long restaurantId,
                                           @PathVariable Long dishId,
                                           @Valid @RequestBody DishUpdateRequestDto dishUpdateRequestDto) {
        dishHandler.updateDish(dishId, dishUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }
}
