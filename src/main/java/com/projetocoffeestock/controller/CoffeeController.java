package com.projetocoffeestock.controller;

import lombok.AllArgsConstructor;
import com.projetocoffeestock.dto.CoffeeDTO;
import com.projetocoffeestock.dto.QuantityDTO;
import com.projetocoffeestock.exception.CoffeeAlreadyRegisteredException;
import com.projetocoffeestock.exception.CoffeeNotFoundException;
import com.projetocoffeestock.exception.CoffeeStockExceededException;
import com.projetocoffeestock.service.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coffees")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CoffeeController implements CoffeeControllerDocs {

    private final CoffeeService coffeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoffeeDTO createCoffee(@RequestBody @Valid CoffeeDTO coffeeDTO) throws CoffeeAlreadyRegisteredException {
        return coffeeService.createCoffee(coffeeDTO);
    }

    @GetMapping("/{name}")
    public CoffeeDTO findByName(@PathVariable String name) throws CoffeeNotFoundException {
        return coffeeService.findByName(name);
    }

    @GetMapping
    public List<CoffeeDTO> listCoffees() {
        return coffeeService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws CoffeeNotFoundException {
        coffeeService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public CoffeeDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws CoffeeNotFoundException, CoffeeStockExceededException {
        return coffeeService.increment(id, quantityDTO.getQuantity());
    }
}
