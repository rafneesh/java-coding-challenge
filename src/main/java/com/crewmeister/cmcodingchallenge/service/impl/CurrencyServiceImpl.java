package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.repository.CurrencyRepository;
import com.crewmeister.cmcodingchallenge.service.CurrencyDataFeed;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CurrencyDataFeed dataFeed;

    @PostConstruct
    public void init() {
        log.info("init in CurrencyService STARTS");
        loadAllCurrencies();
        log.info("init in CurrencyService DONE");
    }

    @Scheduled(cron = "${cron.expression}")
    public void execute() {
        log.info("execute in CurrencyService STARTS");
        loadAllCurrencies();
        log.info("execute in CurrencyService DONE");
    }

    private void loadAllCurrencies(){

        currencyRepository.saveAll(dataFeed.loadCurrencies());
    }

    public ResponseEntity<ArrayList<CurrencyHolder>> getCurrencies() {

       return new ResponseEntity<>((ArrayList<CurrencyHolder>) currencyRepository.findAll(), HttpStatus.OK);
    }

    public CurrencyHolder save(CurrencyHolder currency) {

        return currencyRepository.save(currency);
    }

    public void saveAll(List<CurrencyHolder> currencies) {

        currencyRepository.saveAll(currencies);
    }

}
