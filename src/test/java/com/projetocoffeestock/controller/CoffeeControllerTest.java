package com.projetocoffeestock.controller;

import com.projetocoffeestock.builder.CoffeeDTOBuilder;
import com.projetocoffeestock.dto.CoffeeDTO;
import com.projetocoffeestock.dto.QuantityDTO;
import com.projetocoffeestock.exception.CoffeeNotFoundException;
import com.projetocoffeestock.exception.CoffeeStockExceededException;
import com.projetocoffeestock.service.CoffeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static com.projetocoffeestock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CoffeeControllerTest {

    private static final String COFFEE_API_URL_PATH = "/api/v1/coffees";
    private static final long VALID_COFFEE_ID = 1L;
    private static final long INVALID_COFFEE_ID = 2l;
    private static final String COFFEE_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String COFFEE_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private CoffeeService coffeeService;

    @InjectMocks
    private CoffeeController coffeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(coffeeController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenABeerIsCreated() throws Exception {
        // given
        CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();

        // when
        when(coffeeService.createCoffee(coffeeDTO)).thenReturn(coffeeDTO);

        // then
        mockMvc.perform(post(COFFEE_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(coffeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(coffeeDTO.getName())))
                .andExpect(jsonPath("$.brand", is(coffeeDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(coffeeDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        coffeeDTO.setBrand(null);

        // then
        mockMvc.perform(post(COFFEE_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(coffeeDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();

        //when
        when(coffeeService.findByName(coffeeDTO.getName())).thenReturn(coffeeDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(COFFEE_API_URL_PATH + "/" + coffeeDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(coffeeDTO.getName())))
                .andExpect(jsonPath("$.brand", is(coffeeDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(coffeeDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();

        //when
        when(coffeeService.findByName(coffeeDTO.getName())).thenThrow(CoffeeNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(COFFEE_API_URL_PATH + "/" + coffeeDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();

        //when
        when(coffeeService.listAll()).thenReturn(Collections.singletonList(coffeeDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(COFFEE_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(coffeeDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(coffeeDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(coffeeDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();

        //when
        when(coffeeService.listAll()).thenReturn(Collections.singletonList(coffeeDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(COFFEE_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();

        //when
        doNothing().when(coffeeService).deleteById(coffeeDTO.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(COFFEE_API_URL_PATH + "/" + coffeeDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(CoffeeNotFoundException.class).when(coffeeService).deleteById(INVALID_COFFEE_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(COFFEE_API_URL_PATH + "/" + INVALID_COFFEE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
        coffeeDTO.setQuantity(coffeeDTO.getQuantity() + quantityDTO.getQuantity());

        when(coffeeService.increment(VALID_COFFEE_ID, quantityDTO.getQuantity())).thenReturn(coffeeDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(COFFEE_API_URL_PATH + "/" + VALID_COFFEE_ID + COFFEE_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(coffeeDTO.getName())))
                .andExpect(jsonPath("$.brand", is(coffeeDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(coffeeDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(coffeeDTO.getQuantity())));
    }
}