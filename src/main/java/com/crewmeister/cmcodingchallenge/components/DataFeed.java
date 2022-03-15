package com.crewmeister.cmcodingchallenge.components;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;

import java.util.Set;

public interface DataFeed {

    Set<CurrencyHolder> loadCurrencies();

    Set<ExchangeRate> loadExchangeRates();

}
