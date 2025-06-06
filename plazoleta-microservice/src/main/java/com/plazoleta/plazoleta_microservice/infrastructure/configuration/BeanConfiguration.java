package com.plazoleta.plazoleta_microservice.infrastructure.configuration;

import com.plazoleta.plazoleta_microservice.application.handler.ICategoryHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IDishHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IOrderHandler;
import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import com.plazoleta.plazoleta_microservice.application.handler.impl.CategoryHandlerImpl;
import com.plazoleta.plazoleta_microservice.application.handler.impl.DishHandlerImpl;
import com.plazoleta.plazoleta_microservice.application.handler.impl.OrderHandlerImpl;
import com.plazoleta.plazoleta_microservice.application.handler.impl.RestaurantHandlerImpl;
import com.plazoleta.plazoleta_microservice.application.mapper.*;
import com.plazoleta.plazoleta_microservice.domain.api.ICategoryServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IOrderServicePort;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.spi.*;
import com.plazoleta.plazoleta_microservice.domain.usecase.CategoryUseCase;
import com.plazoleta.plazoleta_microservice.domain.usecase.DishUseCase;
import com.plazoleta.plazoleta_microservice.domain.usecase.OrderUseCase;
import com.plazoleta.plazoleta_microservice.domain.usecase.RestaurantUseCase;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.IUserFeignClient;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.CategoryJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.*;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;
    private final IRestaurantResumeResponseMapper restaurantResumeResponseMapper;
    private final ICategoryRepository categoryRepository;
    private final ICategoryEntityMapper categoryEntityMapper;
    private final ICategoryRequestMapper categoryRequestMapper;
    private final ICategoryResponseMapper categoryResponseMapper;
    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;
    private final IDishUpdateRequestMapper dishUpdateRequestMapper;
    private final IOrderDishRequestMapper orderDishRequestMapper;
    private final IOrderRepository orderRepository;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;
    private final IOrderEntityMapper orderEntityMapper;
    private final IOrderDishRepository orderDishRepository;
    private final IOrderDishEntityMapper orderDishEntityMapper;
    private final IUserFeignClient userFeignClient;
    private final IAuthenticatedUserPort authenticatedUserPort;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort(IRestaurantPersistencePort restaurantPersistencePort,
                                                        IUserServiceClientPort userSecurityPort) {
        return new RestaurantUseCase(restaurantPersistencePort, userSecurityPort);
    }

    @Bean
    public IRestaurantHandler restaurantHandler(IRestaurantServicePort restaurantServicePort) {
        return new RestaurantHandlerImpl(restaurantServicePort, restaurantRequestMapper, restaurantResponseMapper, restaurantResumeResponseMapper);
    }

    @Bean
    public ICategoryPersistencePort categoryPersistencePort() {
        return new CategoryJpaAdapter(categoryRepository, dishRepository, categoryEntityMapper);
    }

    @Bean
    public ICategoryServicePort categoryServicePort(ICategoryPersistencePort categoryPersistencePort) {
        return new CategoryUseCase(categoryPersistencePort);
    }

    @Bean
    public ICategoryHandler categoryHandler(ICategoryServicePort categoryServicePort) {
        return new CategoryHandlerImpl(categoryServicePort, categoryRequestMapper, categoryResponseMapper);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IDishServicePort dishServicePort(IDishPersistencePort dishPersistencePort, ICategoryPersistencePort categoryPersistencePort, IRestaurantPersistencePort restaurantPersistencePort,
                                            IAuthenticatedUserPort authenticatedUserPort) {
        return new DishUseCase(categoryPersistencePort, dishPersistencePort, restaurantPersistencePort, authenticatedUserPort);
    }

    @Bean
    public IDishHandler dishHandler(IDishServicePort dishServicePort) {
        return new DishHandlerImpl(dishServicePort, dishRequestMapper, dishUpdateRequestMapper, dishResponseMapper);
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, dishRepository,orderDishRepository, orderEntityMapper);
    }

    @Bean
    public IOrderServicePort orderServicePort(IOrderPersistencePort orderPersistencePort, IDishPersistencePort dishPersistencePort,
                                              IRestaurantPersistencePort restaurantPersistencePort, IAuthenticatedUserPort authenticatedUserPort, IUserServiceClientPort userServiceClientPort) {
        return new OrderUseCase(orderPersistencePort, dishPersistencePort, restaurantPersistencePort, authenticatedUserPort, userServiceClientPort);
    }

    @Bean
    public IOrderHandler orderHandler(IOrderServicePort orderServicePort) {
        return new OrderHandlerImpl(orderServicePort, orderRequestMapper, orderResponseMapper);
    }

}