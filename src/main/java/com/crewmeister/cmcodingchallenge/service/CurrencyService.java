package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface CurrencyService {

    ResponseEntity<ArrayList<CurrencyHolder>> getCurrencies();

    CurrencyHolder save(CurrencyHolder currency);

    void saveAll(List<CurrencyHolder> currencies);
}
