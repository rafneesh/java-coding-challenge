package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;


@DependsOn("currencyService")
public interface ExchangeRateService {

    ResponseEntity<ArrayList<ExchangeRate>> getExchangeRates();

    ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDate(Date date);

    ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDateAndCurrencyFrom(Date date, Currency currency);

    ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDateAndCurrencyFromAndCurrencyTo(Date date, Currency currencyFrom, Currency currencyTo);

    ResponseEntity<HashMap<String,BigDecimal>> getAmountConvertedInEuro(Date date, String currencyFrom, BigDecimal amountToBeConverted);
}
