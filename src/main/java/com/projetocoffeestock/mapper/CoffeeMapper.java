package com.projetocoffeestock.mapper;

import com.projetocoffeestock.dto.CoffeeDTO;
import com.projetocoffeestock.entity.Coffee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CoffeeMapper {

    CoffeeMapper INSTANCE = Mappers.getMapper(CoffeeMapper.class);

    Coffee toModel(CoffeeDTO coffeeDTO);

    CoffeeDTO toDTO(Coffee coffee);
}
