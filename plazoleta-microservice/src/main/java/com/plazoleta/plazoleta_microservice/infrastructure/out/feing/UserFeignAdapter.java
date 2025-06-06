package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.domain.model.User;
import com.plazoleta.plazoleta_microservice.domain.spi.IUserServiceClientPort;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.UserServiceUnavailableException;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserFeignAdapter implements IUserServiceClientPort {

    private final IUserFeignClient userFeignClient;

    @Override
    public User findUserById(Long id) {
        try {
            return userFeignClient.getUserById(id);
        } catch (FeignException feignException) {
            throw new UserServiceUnavailableException("User service is currently unavailable", feignException);
        }
    }
}
