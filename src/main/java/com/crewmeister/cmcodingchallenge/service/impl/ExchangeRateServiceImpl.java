package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.exception.ExchangeRateNotFound;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateDataFeed;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ExchangeRateDataFeed dataFeed;

    @Autowired
    CurrencyHolder baseCurrency;

    @PostConstruct
    public void init() {
        log.info("init in ExchangeRateService STARTS");
        loadAllExchangeRates();
        log.info("init in ExchangeRateService DONE");
    }

    @Scheduled(cron = "${cron.expression}")
    public void execute() {
        log.info("execute in ExchangeRateService STARTS");
        loadAllExchangeRates();
        log.info("execute in ExchangeRateService DONE");
    }

    private void loadAllExchangeRates(){

        exchangeRateRepository.saveAll(dataFeed.loadExchangeRates());
    }

    public ResponseEntity<ArrayList<ExchangeRate>> getExchangeRates() {

        return new ResponseEntity<>((ArrayList<ExchangeRate>) exchangeRateRepository.findAll(), HttpStatus.OK);
    }


    public ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDate(Date date) {

        return new ResponseEntity<>((ArrayList<ExchangeRate>) exchangeRateRepository.findAllByDate(date), HttpStatus.OK);
    }

    public ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDateAndCurrencyFrom(Date date, Currency currency) {

        return new ResponseEntity<>((ArrayList<ExchangeRate>) exchangeRateRepository.findAllByDateAndCurFrom(date, currency), HttpStatus.OK);
    }

    public ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDateAndCurrencyFromAndCurrencyTo(Date date, Currency currencyFrom, Currency currencyTo) {

        return new ResponseEntity<>((ArrayList<ExchangeRate>) exchangeRateRepository.findAllByDateAndCurFromAndCurTo(date, currencyFrom, currencyTo), HttpStatus.OK);
    }

    public ResponseEntity<HashMap<String,BigDecimal>> getAmountConvertedInEuro(Date date, String currencyFrom, BigDecimal amountToBeConverted) {

        Currency euroCurrency = Currency.getInstance("EUR");

        HashMap<String, BigDecimal> convertedResult = new HashMap<>();

        try {

            var currency = Currency.getInstance(currencyFrom);

            convertedResult.put(currency.getDisplayName(), amountToBeConverted);

        }catch (IllegalArgumentException e){

            throw new CurrencyNotFoundException();
        }

        List<ExchangeRate> exchangeRateList = exchangeRateRepository.findAllByDateAndCurFromAndCurTo(date, euroCurrency,Currency.getInstance(currencyFrom));

        if (exchangeRateList.isEmpty()) {

            throw new ExchangeRateNotFound();
        }

        ExchangeRate exchangeRate = exchangeRateList.get(0);

        BigDecimal amount = amountToBeConverted.divide(exchangeRate.getRate(), 5, RoundingMode.HALF_UP);

        convertedResult.put(euroCurrency.getDisplayName(),amount);

        return new ResponseEntity<>(convertedResult, HttpStatus.OK);
    }


}
