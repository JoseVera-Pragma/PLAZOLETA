package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.domain.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-microservice", url = "${user.service.url}")
public interface IUserFeignClient {

    @GetMapping("/users/{userId}")
    User getUserById(@PathVariable("userId") Long userId);
}
