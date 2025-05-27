package com.plazoleta.plazoleta_microservice.infrastructure.configuration;

import com.plazoleta.plazoleta_microservice.application.handler.IRestaurantHandler;
import com.plazoleta.plazoleta_microservice.application.handler.impl.RestaurantHandlerImpl;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantRequestMapper;
import com.plazoleta.plazoleta_microservice.application.mapper.IRestaurantResponseMapper;
import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IUserSecurityPort;
import com.plazoleta.plazoleta_microservice.domain.usecase.RestaurantUseCase;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.IUserFeignClient;
import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.UserFeignAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IUserFeignClient userFeignClient;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IUserSecurityPort userSecurityPort() {
        return new UserFeignAdapter(userFeignClient);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort(IRestaurantPersistencePort restaurantPersistencePort,
                                                        IUserSecurityPort userSecurityPort) {
        return new RestaurantUseCase(restaurantPersistencePort, userSecurityPort);
    }

    @Bean
    public IRestaurantHandler restaurantHandler(IRestaurantServicePort restaurantServicePort) {
        return new RestaurantHandlerImpl(restaurantServicePort, restaurantRequestMapper, restaurantResponseMapper);
    }
}