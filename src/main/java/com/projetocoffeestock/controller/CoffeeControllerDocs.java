package com.projetocoffeestock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.projetocoffeestock.dto.CoffeeDTO;
import com.projetocoffeestock.dto.QuantityDTO;
import com.projetocoffeestock.exception.CoffeeAlreadyRegisteredException;
import com.projetocoffeestock.exception.CoffeeNotFoundException;
import com.projetocoffeestock.exception.CoffeeStockExceededException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api("Manages coffee stock")
public interface CoffeeControllerDocs {

    @ApiOperation(value = "Coffee creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success coffee creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    CoffeeDTO createCoffee(CoffeeDTO coffeeDTO) throws CoffeeAlreadyRegisteredException;

    @ApiOperation(value = "Returns coffee found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success coffee found in the system"),
            @ApiResponse(code = 404, message = "Coffee with given name not found.")
    })
    CoffeeDTO findByName(@PathVariable String name) throws CoffeeNotFoundException;

    @ApiOperation(value = "Returns a list of all coffees registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all coffees registered in the system"),
    })
    List<CoffeeDTO> listCoffees();

    @ApiOperation(value = "Delete a coffee found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success coffee deleted in the system"),
            @ApiResponse(code = 404, message = "Coffee with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws CoffeeNotFoundException;
}

