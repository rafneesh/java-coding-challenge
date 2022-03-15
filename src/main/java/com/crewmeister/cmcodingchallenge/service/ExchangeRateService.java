package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.components.DataFeed;
import com.crewmeister.cmcodingchallenge.config.Config;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exception.CurrencyNotFoundException;
import com.crewmeister.cmcodingchallenge.exception.ExchangeRateNotFound;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@DependsOn("currencyService")
public class ExchangeRateService {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private DataFeed dataFeed;

    @Autowired
    Config config;

    @PostConstruct
    public void init() {
        log.info("init in ExchangeRateService STARTS");
        exchangeRateRepository.saveAll(dataFeed.loadExchangeRates());
        log.info("init in ExchangeRateService DONE");
    }

    @Scheduled(cron = "${cron.expression}")
    public void execute() {
        log.info("execute in ExchangeRateService STARTS");
        exchangeRateRepository.saveAll(dataFeed.loadExchangeRates());
        log.info("execute in ExchangeRateService DONE");
    }

    public ResponseEntity<ArrayList<ExchangeRate>> getExchangeRates() {

        return new ResponseEntity<ArrayList<ExchangeRate>>((ArrayList<ExchangeRate>) exchangeRateRepository.findAll(), HttpStatus.OK);
    }


    public ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDate(Date date) {

        return new ResponseEntity<ArrayList<ExchangeRate>>((ArrayList<ExchangeRate>) exchangeRateRepository.findAllByDate(date), HttpStatus.OK);
    }

    public ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDateAndCurrencyTo(Date date, Currency currency) {

        return new ResponseEntity<ArrayList<ExchangeRate>>((ArrayList<ExchangeRate>) exchangeRateRepository.findAllByDateAndCurTo(date, currency), HttpStatus.OK);
    }

    public ResponseEntity<ArrayList<ExchangeRate>> getExchangeRatesForDateAndCurrencyFromAndCurrencyTo(Date date, Currency currencyFrom, Currency currencyTo) {

        return new ResponseEntity<ArrayList<ExchangeRate>>((ArrayList<ExchangeRate>) exchangeRateRepository.findAllByDateAndCurFromAndCurTo(date, currencyFrom, currencyTo), HttpStatus.OK);
    }

    public ResponseEntity<String> getAmountConvertedInEuro(Date date, String currencyFrom, Double amountToBeConverted) {

        try {

            Currency currency = Currency.getInstance(currencyFrom);

        }catch (IllegalArgumentException e){

            throw new CurrencyNotFoundException();
        }

        List<ExchangeRate> exchangeRateList = exchangeRateRepository.findAllByDateAndCurFromAndCurTo(date, Currency.getInstance(currencyFrom), config.getBaseCurrencyHolder().getCurrency());

        if (exchangeRateList.size() < 1) {

            throw new ExchangeRateNotFound();
        }

        ExchangeRate exchangeRate = exchangeRateList.get(0);

        Double amount = amountToBeConverted / exchangeRate.getRate();

        String amountWithCurrency = amount + config.getBaseCurrencyHolder().getCurrency().getSymbol();

        return new ResponseEntity<String>(amountWithCurrency, HttpStatus.OK);
    }


}
