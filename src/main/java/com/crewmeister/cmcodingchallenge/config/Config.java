package com.crewmeister.cmcodingchallenge.config;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Currency;

@Configuration
@EnableScheduling
public class Config {

    @Value("${currency.base.name:EUR}")
    String baseCurrencyName;


    public CurrencyHolder getBaseCurrencyHolder() {


        Currency baseCurrency = Currency.getInstance(baseCurrencyName);

        CurrencyHolder currencyHolder = new CurrencyHolder();
        currencyHolder.setCurrency(baseCurrency);

        return currencyHolder;
    }
}
