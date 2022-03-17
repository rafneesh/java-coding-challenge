package com.crewmeister.cmcodingchallenge.components.data;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;

import java.util.Set;

@FunctionalInterface
public interface ExchangeRateDataFeed {

    Set<ExchangeRate> loadExchangeRates();

}
