package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController()
@RequestMapping("/api")
@Slf4j
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;

    @GetMapping("/currencies")
    public ResponseEntity<ArrayList<CurrencyHolder>> getCurrencies() {
        log.info("Inside getCurrencies from CurrencyController");
        return currencyService.getCurrencies();
    }


}
