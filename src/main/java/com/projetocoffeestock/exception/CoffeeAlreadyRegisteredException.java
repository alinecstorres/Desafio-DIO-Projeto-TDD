package com.projetocoffeestock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CoffeeAlreadyRegisteredException extends Exception{

    public CoffeeAlreadyRegisteredException(String coffeeName) {
        super(String.format("Coffee with name %s already registered in the system.", coffeeName));
    }
}

