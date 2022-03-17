package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;

import java.util.Set;

@FunctionalInterface
public interface ExchangeRateDataFeed {

    Set<ExchangeRate> loadExchangeRates();

}
