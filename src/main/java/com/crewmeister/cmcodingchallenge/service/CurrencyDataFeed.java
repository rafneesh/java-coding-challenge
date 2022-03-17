package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;

import java.util.Set;

@FunctionalInterface
public interface CurrencyDataFeed {

    Set<CurrencyHolder> loadCurrencies();

}
