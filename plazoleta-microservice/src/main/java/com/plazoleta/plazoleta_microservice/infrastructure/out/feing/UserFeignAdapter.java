package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.domain.model.User;
import com.plazoleta.plazoleta_microservice.domain.spi.IUserSecurityPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserFeignAdapter implements IUserSecurityPort {

    private final IUserFeignClient userFeignClient;

    @Override
    public User getUserById(Long id) {
        return userFeignClient.getUserById(id);
    }
}
