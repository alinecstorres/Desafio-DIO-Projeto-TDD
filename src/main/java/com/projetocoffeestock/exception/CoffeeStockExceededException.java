package com.projetocoffeestock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CoffeeStockExceededException extends Exception {

    public CoffeeStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Coffees with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}

