package com.projetocoffeestock.service;

import lombok.AllArgsConstructor;
import com.projetocoffeestock.dto.CoffeeDTO;
import com.projetocoffeestock.entity.Coffee;
import com.projetocoffeestock.exception.CoffeeAlreadyRegisteredException;
import com.projetocoffeestock.exception.CoffeeNotFoundException;
import com.projetocoffeestock.exception.CoffeeStockExceededException;
import com.projetocoffeestock.mapper.CoffeeMapper;
import com.projetocoffeestock.repository.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;
    private final CoffeeMapper coffeeMapper = CoffeeMapper.INSTANCE;

    public CoffeeDTO createCoffee(CoffeeDTO coffeeDTO) throws CoffeeAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(coffeeDTO.getName());
        Coffee coffee = coffeeMapper.toModel(coffeeDTO);
        Coffee savedCoffee = coffeeRepository.save(coffee);
        return coffeeMapper.toDTO(savedCoffee);
    }

    public CoffeeDTO findByName(String name) throws CoffeeNotFoundException {
        Coffee foundCoffee = coffeeRepository.findByName(name)
                .orElseThrow(() -> new CoffeeNotFoundException(name));
        return coffeeMapper.toDTO(foundCoffee);
    }

    public List<CoffeeDTO> listAll() {
        return coffeeRepository.findAll()
                .stream()
                .map(coffeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws CoffeeNotFoundException {
        verifyIfExists(id);
        coffeeRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws CoffeeAlreadyRegisteredException {
        Optional<Coffee> optSavedCoffee = coffeeRepository.findByName(name);
        if (optSavedCoffee.isPresent()) {
            throw new CoffeeAlreadyRegisteredException(name);
        }
    }

    private Coffee verifyIfExists(Long id) throws CoffeeNotFoundException {
        return coffeeRepository.findById(id)
                .orElseThrow(() -> new CoffeeNotFoundException(id));
    }

    public CoffeeDTO increment(Long id, int quantityToIncrement) throws CoffeeNotFoundException, CoffeeStockExceededException {
        Coffee coffeeToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + coffeeToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= coffeeToIncrementStock.getMax()) {
            coffeeToIncrementStock.setQuantity(coffeeToIncrementStock.getQuantity() + quantityToIncrement);
            Coffee incrementedCoffeeStock = coffeeRepository.save(coffeeToIncrementStock);
            return coffeeMapper.toDTO(incrementedCoffeeStock);
        }
        throw new CoffeeStockExceededException(id, quantityToIncrement);
    }
}

