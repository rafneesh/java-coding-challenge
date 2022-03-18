package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.exception.ExchangeRateNotFound;
import com.crewmeister.cmcodingchallenge.service.CurrencyDataFeed;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;

@WebMvcTest(controllers = ExchangeController.class)
public class ExchangeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private CurrencyDataFeed dataFeed;



    @Test
    @DisplayName("Load exchange rates - api/exchange/rates")
    public void getExchangeRates() throws Exception{

        ExchangeRate exchangeRateOne = new ExchangeRate(1l, Currency.getInstance("EUR"), Currency.getInstance("INR"), BigDecimal.valueOf(83.9875), new Date(), "");
        ExchangeRate exchangeRateTwo = new ExchangeRate(2l, Currency.getInstance("EUR"), Currency.getInstance("USD"), BigDecimal.valueOf(1.11), new Date(), "");
        ExchangeRate exchangeRateThree = new ExchangeRate(3l, Currency.getInstance("EUR"), Currency.getInstance("GBP"), BigDecimal.valueOf(0.84), new Date(), "");

        ResponseEntity response = new ResponseEntity<>(Arrays.asList(exchangeRateOne,exchangeRateTwo,exchangeRateThree), HttpStatus.OK);

        Mockito.when(exchangeRateService.getExchangeRates()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/rates"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].curFrom", Matchers.is("EUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].curTo", Matchers.is("INR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rate", Matchers.is(83.9875)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].curFrom", Matchers.is("EUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].curTo", Matchers.is("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].rate", Matchers.is(1.11)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].curFrom", Matchers.is("EUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].curTo", Matchers.is("GBP")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].rate", Matchers.is(0.84)))
        ;

    }

    @Test
    @DisplayName("Load exchange rates while rates in Db is 0  - api/exchange/rates")
    public void getExchangeRatesDbEmpty() throws Exception{

        ResponseEntity response = new ResponseEntity<>(Arrays.asList(), HttpStatus.OK);

        Mockito.when(exchangeRateService.getExchangeRates()).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/rates"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(0)))
        ;

    }

    @Test
    @DisplayName("Load exchange rates while Db is down  - api/exchange/rates")
    public void getExchangeRatesDbDown() throws Exception{

        ResponseEntity response = new ResponseEntity<>(Arrays.asList(), HttpStatus.OK);

        Mockito.when(exchangeRateService.getExchangeRates()).thenThrow(new DataAccessException("Db is down") {
        });

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/rates"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Database is down")))
        ;

    }

    @Test
    @DisplayName("Load exchange rates for a particular day - api/exchange/rates/{date}")
    public void getExchangeRatesForDate() throws Exception{

        ExchangeRate exchangeRateOne = new ExchangeRate(1l, Currency.getInstance("EUR"), Currency.getInstance("INR"), BigDecimal.valueOf(83.9875), new Date(2022,03,18), "");
        ExchangeRate exchangeRateTwo = new ExchangeRate(2l, Currency.getInstance("EUR"), Currency.getInstance("USD"), BigDecimal.valueOf(1.11), new Date(2022,03,18), "");
        ExchangeRate exchangeRateThree = new ExchangeRate(3l, Currency.getInstance("EUR"), Currency.getInstance("GBP"), BigDecimal.valueOf(0.84), new Date(2022,03,18), "");

        ResponseEntity response = new ResponseEntity<>(Arrays.asList(exchangeRateOne,exchangeRateTwo,exchangeRateThree), HttpStatus.OK);

        Mockito.when(exchangeRateService.getExchangeRatesForDate(Mockito.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/rates/{date}", "2022-04-18"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].curFrom", Matchers.is("EUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].curTo", Matchers.is("INR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rate", Matchers.is(83.9875)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].curFrom", Matchers.is("EUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].curTo", Matchers.is("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].rate", Matchers.is(1.11)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].curFrom", Matchers.is("EUR")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].curTo", Matchers.is("GBP")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].rate", Matchers.is(0.84)))
        ;

    }

    @Test
    @DisplayName("Load exchange rates for a particular day while data in Db is 0- api/exchange/rates/{date}")
    public void getExchangeRatesForDateDbEmpty() throws Exception{

        ExchangeRate exchangeRateOne = new ExchangeRate(1l, Currency.getInstance("EUR"), Currency.getInstance("INR"), BigDecimal.valueOf(83.9875), new Date(2022,03,18), "");
        ExchangeRate exchangeRateTwo = new ExchangeRate(2l, Currency.getInstance("EUR"), Currency.getInstance("USD"), BigDecimal.valueOf(1.11), new Date(2022,03,18), "");
        ExchangeRate exchangeRateThree = new ExchangeRate(3l, Currency.getInstance("EUR"), Currency.getInstance("GBP"), BigDecimal.valueOf(0.84), new Date(2022,03,18), "");

        ResponseEntity response = new ResponseEntity<>(Arrays.asList(exchangeRateOne,exchangeRateTwo,exchangeRateThree), HttpStatus.OK);

        Mockito.when(exchangeRateService.getExchangeRatesForDate(Mockito.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/rates/{date}", "2022-04-18"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)))
        ;

    }

    @Test
    @DisplayName("Load exchange rates for a particular day while Db is down- api/exchange/rates/{date}")
    public void getExchangeRatesForDateDbDown() throws Exception{

        Mockito.when(exchangeRateService.getExchangeRatesForDate(Mockito.any())).thenThrow(new DataAccessException("Db is down") {
        });

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/rates/{date}", "2022-04-18"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Database is down")))
        ;

    }

    @Test
    @DisplayName("Calculate a currency converted to EURO for a particular day rate - api/exchange/rates/{date}")
    public void getEuroConvertedAmountForDate() throws Exception{

        HashMap<String, BigDecimal> convertedResult = new HashMap<>();

        convertedResult.put(Currency.getInstance("INR").getDisplayName(), BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(83.9875), 5, RoundingMode.HALF_UP);

        convertedResult.put(Currency.getInstance("EUR").getDisplayName(),amount);

        Mockito.when(exchangeRateService.getAmountConvertedInEuro(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ResponseEntity<>(convertedResult, HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/euro/converter/{date}", "2022-04-18").param("currencyFrom", "INR").param("amount","100"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.['Indian Rupee']", Matchers.not(Matchers.notANumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.Euro", Matchers.not(Matchers.notANumber())));
        ;

    }

    @Test
    @DisplayName("Calculate a currency converted to EURO for a particular day rate while rate is not available - api/exchange/rates/{date}")
    public void getEuroConvertedAmountForDateExchangeRateNotFound() throws Exception{

        Mockito.when(exchangeRateService.getAmountConvertedInEuro(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new ExchangeRateNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/euro/converter/{date}", "2035-04-16").param("currencyFrom", "INR").param("amount","100"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Exchange rate not found")))
        ;

    }

    @Test
    @DisplayName("Calculate a currency converted to EURO for a particular day rate while currency from is not found - api/exchange/rates/{date}")
    public void getEuroConvertedAmountForDateCurrencyNotFound() throws Exception{

        Mockito.when(exchangeRateService.getAmountConvertedInEuro(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new CurrencyNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/exchange/euro/converter/{date}", "2022-04-18").param("currencyFrom", "INRK").param("amount","100"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Currency not found")))
        ;

    }
}
