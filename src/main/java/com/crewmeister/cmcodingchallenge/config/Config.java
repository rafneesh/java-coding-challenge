package com.crewmeister.cmcodingchallenge.config;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Currency;
import java.util.Locale;

@Configuration
@EnableScheduling
public class Config {

    @Value("${currency.base.name:EUR}")
    String baseCurrencyName;


    @Bean
    public CurrencyHolder baseCurrency() {

        Currency baseCurrency = Currency.getInstance(baseCurrencyName.toUpperCase(Locale.ROOT));

        CurrencyHolder currencyHolder = new CurrencyHolder();
        currencyHolder.setCurrency(baseCurrency);

        return currencyHolder;
    }
}
