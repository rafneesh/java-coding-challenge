package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.service.CurrencyDataFeed;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Currency;

@WebMvcTest(controllers = CurrencyController.class)
public class CurrencyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private CurrencyDataFeed dataFeed;

    @Test
    @DisplayName("Load currencies - /api/currencies")
    public void getCurrencies() throws Exception{

        CurrencyHolder currencyOne = new CurrencyHolder(Currency.getInstance("EUR"));
        CurrencyHolder currencyTwo = new CurrencyHolder(Currency.getInstance("USD"));
        CurrencyHolder currencyThree = new CurrencyHolder(Currency.getInstance("INR"));

        ResponseEntity response = new ResponseEntity<>(Arrays.asList(currencyOne,currencyTwo,currencyThree), HttpStatus.OK);

        Mockito.when(currencyService.getCurrencies()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].currency", Matchers.is("EUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].currency", Matchers.is("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].currency", Matchers.is("INR")))
        ;

    }

    @Test
    @DisplayName("Load currencies while currencies in Db is 0 - /api/currencies")
    public void getCurrenciesDbEmpty() throws Exception{

        ResponseEntity response = new ResponseEntity<>(Arrays.asList(), HttpStatus.OK);

        Mockito.when(currencyService.getCurrencies()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(0)))
        ;

    }

    @Test
    @DisplayName("Load currencies while the db is down - /api/currencies")
    public void getCurrenciesDbDown() throws Exception{

        ResponseEntity response = new ResponseEntity<>(Arrays.asList(), HttpStatus.OK);

        Mockito.when(currencyService.getCurrencies()).thenThrow(new DataAccessException("Db is down") {
        });

        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Database is down")))
        ;

    }

}
