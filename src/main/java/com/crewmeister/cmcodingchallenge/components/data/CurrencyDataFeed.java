package com.crewmeister.cmcodingchallenge.components.data;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;

import java.util.Set;

@FunctionalInterface
public interface CurrencyDataFeed {

    Set<CurrencyHolder> loadCurrencies();

}
