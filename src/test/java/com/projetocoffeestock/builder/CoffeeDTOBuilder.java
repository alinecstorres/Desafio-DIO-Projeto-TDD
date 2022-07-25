package com.projetocoffeestock.builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import com.projetocoffeestock.dto.CoffeeDTO;
import com.projetocoffeestock.enums.CoffeeType;

@Builder
public class CoffeeDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Melitta";

    @Builder.Default
    private String brand = "Fazenda Sta Monica";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private CoffeeType type = CoffeeType.STAMONICA;

    public CoffeeDTO toCoffeeDTO() {
        return new CoffeeDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}

