package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;

@Service
@DependsOn("currencyService")
public interface ExchangeRateService {

    ResponseEntity<ArrayList<ExchangeRate>> getExchangeRates();

    ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDate(Date date);

    ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDateAndCurrencyTo(Date date, Currency currency);

    ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDateAndCurrencyFromAndCurrencyTo(Date date, Currency currencyFrom, Currency currencyTo);

    ResponseEntity<HashMap> getAmountConvertedInEuro(Date date, String currencyFrom, BigDecimal amountToBeConverted);
}
