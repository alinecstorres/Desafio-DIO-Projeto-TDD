package com.projetocoffeestock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoffeeType {

    STAMONICA("StaMonica"),
    BAGGIO("Baggio"),
    DUTRA("Dutra"),
    SAOBRAZ("SaoBraz"),
    STACLARA("StaClara"),
    MELITTA("Melitta"),
    TRESCORACOES("TresCoracoes");

    private final String description;
}
