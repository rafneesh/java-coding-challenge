package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.components.data.ExchangeRateDataFeed;
import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
@DependsOn("currencyService")
public class ExchangeRateService {

    @Autowired
    CurrencyService currencyService;

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

    public ResponseEntity<HashMap> getAmountConvertedInEuro(Date date, String currencyFrom, BigDecimal amountToBeConverted) {

        HashMap<String, BigDecimal> convertedResult = new HashMap();

        try {

            var currency = Currency.getInstance(currencyFrom);

            convertedResult.put(currency.getDisplayName(), amountToBeConverted);

        }catch (IllegalArgumentException e){

            throw new CurrencyNotFoundException();
        }

        List<ExchangeRate> exchangeRateList = exchangeRateRepository.findAllByDateAndCurFromAndCurTo(date, Currency.getInstance(currencyFrom), baseCurrency.getCurrency());

        if (exchangeRateList.size() < 1) {

            throw new ExchangeRateNotFound();
        }

        ExchangeRate exchangeRate = exchangeRateList.get(0);

        BigDecimal amount = amountToBeConverted.divide(exchangeRate.getRate(), 5, RoundingMode.HALF_UP);

        convertedResult.put(baseCurrency.getCurrency().getDisplayName(),amount);

        return new ResponseEntity<HashMap>(convertedResult, HttpStatus.OK);
    }


}
