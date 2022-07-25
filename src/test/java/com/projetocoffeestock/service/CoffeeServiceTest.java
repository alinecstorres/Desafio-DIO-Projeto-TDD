package com.projetocoffeestock.service;

import com.projetocoffeestock.builder.CoffeeDTOBuilder;
import com.projetocoffeestock.dto.CoffeeDTO;
import com.projetocoffeestock.entity.Coffee;
import com.projetocoffeestock.exception.CoffeeAlreadyRegisteredException;
import com.projetocoffeestock.exception.CoffeeNotFoundException;
import com.projetocoffeestock.exception.CoffeeStockExceededException;
import com.projetocoffeestock.mapper.CoffeeMapper;
import com.projetocoffeestock.repository.CoffeeRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoffeeServiceTest {

    private static final long INVALID_COFFEE_ID = 1L;

    @Mock
    private CoffeeRepository coffeeRepository;

    private CoffeeMapper coffeeMapper = CoffeeMapper.INSTANCE;

    @InjectMocks
    private CoffeeService coffeeService;

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws CoffeeAlreadyRegisteredException {
        // given
        CoffeeDTO expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        Coffee expectedSavedCoffee = coffeeMapper.toModel(expectedCoffeeDTO);

        // when
        when(coffeeRepository.findByName(expectedCoffeeDTO.getName())).thenReturn(Optional.empty());
        when(coffeeRepository.save(expectedSavedCoffee)).thenReturn(expectedSavedCoffee);

        //then
        CoffeeDTO createdCoffeeDTO = coffeeService.createCoffee(expectedCoffeeDTO);

        assertThat(createdCoffeeDTO.getId(), is(equalTo(expectedCoffeeDTO.getId())));
        assertThat(createdCoffeeDTO.getName(), is(equalTo(expectedCoffeeDTO.getName())));
        assertThat(createdCoffeeDTO.getQuantity(), is(equalTo(expectedCoffeeDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
        // given
        CoffeeDTO expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        Coffee duplicatedCoffee = coffeeMapper.toModel(expectedCoffeeDTO);

        // when
        when(coffeeRepository.findByName(expectedCoffeeDTO.getName())).thenReturn(Optional.of(duplicatedCoffee));

        // then
        assertThrows(CoffeeAlreadyRegisteredException.class, () -> coffeeService.createCoffee(expectedCoffeeDTO));
    }

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws CoffeeNotFoundException {
        // given
        CoffeeDTO expectedFoundCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        Coffee expectedFoundCoffee = coffeeMapper.toModel(expectedFoundCoffeeDTO);

        // when
        when(coffeeRepository.findByName(expectedFoundCoffee.getName())).thenReturn(Optional.of(expectedFoundCoffee));

        // then
        CoffeeDTO foundCoffeeDTO = coffeeService.findByName(expectedFoundCoffeeDTO.getName());

        assertThat(foundCoffeeDTO, is(equalTo(expectedFoundCoffeeDTO)));
    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        // given
        CoffeeDTO expectedFoundCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();

        // when
        when(coffeeRepository.findByName(expectedFoundCoffeeDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(CoffeeNotFoundException.class, () -> coffeeService.findByName(expectedFoundCoffeeDTO.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfCoffees() {
        // given
        CoffeeDTO expectedFoundCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        Coffee expectedFoundCoffee = coffeeMapper.toModel(expectedFoundCoffeeDTO);

        //when
        when(coffeeRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundCoffee));

        //then
        List<CoffeeDTO> foundListCoffeesDTO = coffeeService.listAll();

        assertThat(foundListCoffeesDTO, is(not(empty())));
        assertThat(foundListCoffeesDTO.get(0), is(equalTo(expectedFoundCoffeeDTO)));
    }

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() {
        //when
        when(coffeeRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<CoffeeDTO> foundListCoffeesDTO = coffeeService.listAll();

        assertThat(foundListCoffeesDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws CoffeeNotFoundException {
        // given
        CoffeeDTO expectedDeletedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        Coffee expectedDeletedCoffee = coffeeMapper.toModel(expectedDeletedCoffeeDTO);

        // when
        when(coffeeRepository.findById(expectedDeletedCoffeeDTO.getId())).thenReturn(Optional.of(expectedDeletedCoffee));
        doNothing().when(coffeeRepository).deleteById(expectedDeletedCoffeeDTO.getId());

        // then
        coffeeService.deleteById(expectedDeletedCoffeeDTO.getId());

        verify(coffeeRepository, times(1)).findById(expectedDeletedCoffeeDTO.getId());
        verify(coffeeRepository, times(1)).deleteById(expectedDeletedCoffeeDTO.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementCoffeeStock() throws CoffeeNotFoundException, CoffeeStockExceededException {
        //given
        CoffeeDTO expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        Coffee expectedCoffee = coffeeMapper.toModel(expectedCoffeeDTO);

        //when
        when(coffeeRepository.findById(expectedCoffeeDTO.getId())).thenReturn(Optional.of(expectedCoffee));
        when(coffeeRepository.save(expectedCoffee)).thenReturn(expectedCoffee);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedCoffeeDTO.getQuantity() + quantityToIncrement;

        // then
        CoffeeDTO incrementedCoffeeDTO = coffeeService.increment(expectedCoffeeDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedCoffeeDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedCoffeeDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        CoffeeDTO expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        Coffee expectedCoffee = coffeeMapper.toModel(expectedCoffeeDTO);

        when(coffeeRepository.findById(expectedCoffeeDTO.getId())).thenReturn(Optional.of(expectedCoffee));

        int quantityToIncrement = 80;
        assertThrows(CoffeeStockExceededException.class, () -> coffeeService.increment(expectedCoffeeDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        CoffeeDTO expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        Coffee expectedCoffee = coffeeMapper.toModel(expectedCoffeeDTO);

        when(coffeeRepository.findById(expectedCoffeeDTO.getId())).thenReturn(Optional.of(expectedCoffee));

        int quantityToIncrement = 45;
        assertThrows(CoffeeStockExceededException.class, () -> coffeeService.increment(expectedCoffeeDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(coffeeRepository.findById(INVALID_COFFEE_ID)).thenReturn(Optional.empty());

        assertThrows(CoffeeNotFoundException.class, () -> coffeeService.increment(INVALID_COFFEE_ID, quantityToIncrement));
    }
}