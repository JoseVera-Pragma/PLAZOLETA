package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.application.dto.response.DishResponseDto;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishResponseMapper {
    @Mapping(source = "categoryName", target = "category.name")
    Dish toDish(DishResponseDto dto);

    @Mapping(source = "category.name", target = "categoryName")
    DishResponseDto toDishResponse(Dish dish);

    List<DishResponseDto> toDishResponseList(List<Dish> dishList);


    default Page<DishResponseDto> toDishDtoPage(Page<Dish> dishPage) {
        List<DishResponseDto> dtoList = toDishResponseList(dishPage.getContent());

        return new Page<>(
                dtoList,
                dishPage.getPageNumber(),
                dishPage.getPageSize(),
                dishPage.getTotalElements()
        );
    }
}
