package com.crewmeister.cmcodingchallenge.service.impl;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.service.CurrencyDataFeed;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateDataFeed;
import com.crewmeister.cmcodingchallenge.utils.DataFeedUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class BundesBankDataFeedImpl implements CurrencyDataFeed, ExchangeRateDataFeed {

    @Autowired
    CurrencyHolder baseCurrency;

    @Value("${datafeed.bundesbank}")
    String dataFeedLink;

    @Autowired
    DataFeedUtils dataFeedUtils;

    Set<CurrencyHolder> currencies = new HashSet<>();

    Set<ExchangeRate> exchangeRates = new HashSet<>();

    @Override
    public Set<CurrencyHolder> loadCurrencies() {

        log.info("Loading currencies Starts =>" + dataFeedLink);

        //Add the configured base currency else default one
        currencies.add(baseCurrency);

        try {

            dataFeedUtils.parseCurrencyForBundesBank(dataFeedLink, currencies);

            // In case of any IO errors / any parsing exceptions, we want the messages written to the console

        } catch (Exception e) {
            log.error("Exception in Loading currencies =>" + e);
        }

        log.info("Loading currencies Done "+currencies.size());

        return currencies;

    }



    @Override
    public Set<ExchangeRate> loadExchangeRates() {

        log.info("Loading loadExchangeRates Starts");

        try {

            dataFeedUtils.parseExchangeRatesForBundesBank(dataFeedLink, exchangeRates);


        } catch (Exception e) {
            log.warn("Exception in Loading loadExchangeRates => " + e);
        }

        log.info("Loading loadExchangeRates Done " + exchangeRates.size());

        return exchangeRates;

    }


}
