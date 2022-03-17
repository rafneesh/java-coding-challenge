package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@RestController()
@RequestMapping("/api/exchange")
@Slf4j
public class ExchangeController {

    @Autowired
    ExchangeRateService exchangeRateService;

    @GetMapping("/rates")
    public ResponseEntity<ArrayList<ExchangeRate>> getRates(@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Validated Date date) {
        log.info("Inside getRates from ExchangeController " + date);

        if (date != null)
            return exchangeRateService.getExchangeRatesForDate(date);

        return exchangeRateService.getExchangeRates();

    }

    @GetMapping("/rates/{date}")
    public ResponseEntity<ArrayList<ExchangeRate>> getRatesForDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") @Validated Date date) {
        log.info("Inside getRatesForDate from ExchangeController " + date);

        return exchangeRateService.getExchangeRatesForDate(date);

    }

    @GetMapping("/euro/converter/{date}")
    public ResponseEntity<HashMap> getEuroConvertedAmountForDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") @Validated Date date, @RequestParam(value = "currencyFrom", required = true) String currency, @RequestParam(value = "amount", required = true) BigDecimal amount) {

        log.info("Inside getConvertedAmountForDate from ExchangeController " + date + currency + amount);

        return exchangeRateService.getAmountConvertedInEuro(date, currency.toUpperCase(Locale.ROOT), amount);

    }

}
