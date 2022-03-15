package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.components.DataFeed;
import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.repository.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
@Slf4j
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private DataFeed dataFeed;

    @PostConstruct
    public void init() {
        log.info("init in CurrencyService STARTS");
        currencyRepository.saveAll(dataFeed.loadCurrencies());
        log.info("init in CurrencyService DONE");
    }

    @Scheduled(cron = "${cron.expression}")
    public void execute() {
        log.info("execute in CurrencyService STARTS");
        currencyRepository.saveAll(dataFeed.loadCurrencies());
        log.info("execute in CurrencyService DONE");
    }

    public ResponseEntity<ArrayList<CurrencyHolder>> getCurrencies() {

       return new ResponseEntity<ArrayList<CurrencyHolder>>((ArrayList<CurrencyHolder>) currencyRepository.findAll(), HttpStatus.OK);
    }

    public CurrencyHolder save(CurrencyHolder currency) {

        return currencyRepository.save(currency);
    }

    public void saveAll(List<CurrencyHolder> currencies) {

        currencyRepository.saveAll(currencies);
    }

    public Currency get(CurrencyHolder currency) {

        return null;
    }

}
