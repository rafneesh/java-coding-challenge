package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exception.DataFeedException;
import com.crewmeister.cmcodingchallenge.service.impl.BundesBankDataFeedImpl;
import com.crewmeister.cmcodingchallenge.utils.DataFeedUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class BundesBankDataFeedTests {


    @Mock
    DataFeedUtils dataFeedUtils;

    @InjectMocks
    BundesBankDataFeedImpl bundesBankDataFeed;

    @Test
    @DisplayName(value = "Load currencies along with the configured base currency")
    public void loadCurrencies() throws DataFeedException {

        ReflectionTestUtils.setField(bundesBankDataFeed,
                "dataFeedLink", "https://www.bundesbank.de/dynamic/action/en/statistics/time-series-databases/time-series-databases/759784/759784?statisticType=BBK_ITS&listId=www_sdks_b01012_3&treeAnchor=WECHSELKURSE");

        Set<CurrencyHolder> currencies = new HashSet<>(Arrays.asList(new CurrencyHolder(Currency.getInstance("EUR")),new CurrencyHolder(Currency.getInstance("USD")), new CurrencyHolder(Currency.getInstance("INR"))));

        ReflectionTestUtils.setField(bundesBankDataFeed,
                "currencies", currencies);

        Mockito.doNothing().when(dataFeedUtils).parseCurrencyForBundesBank("https://www.bundesbank.de/dynamic/action/en/statistics/time-series-databases/time-series-databases/759784/759784?statisticType=BBK_ITS&listId=www_sdks_b01012_3&treeAnchor=WECHSELKURSE",currencies);

        //Check for size. 3 currencies added + base currency
        Assertions.assertEquals(bundesBankDataFeed.loadCurrencies().size(), 4);


    }

    @Test
    @DisplayName(value = "Load currencies when DataFeedException")
    public void loadCurrenciesDataFeedException() throws DataFeedException {

        ReflectionTestUtils.setField(bundesBankDataFeed,
                "dataFeedLink", "https://www.bundesbank.de/dynamic/action/en/statistics/time-series-databases/time-series-databases/759784/759784?statisticType=BBK_ITS&listId=www_sdks_b01012_3&treeAnchor=WECHSELKURSE");

        Set<CurrencyHolder> currencies = new HashSet<>();

        ReflectionTestUtils.setField(bundesBankDataFeed,
                "currencies", currencies);

        Mockito.doThrow(new DataFeedException("")).when(dataFeedUtils).parseCurrencyForBundesBank(Mockito.any(), Mockito.anySet());

        /*Assertions.assertThrows(DataFeedException.class, () -> {
            bundesBankDataFeed.loadCurrencies();
        });*/
        Assertions.assertEquals(bundesBankDataFeed.loadCurrencies().size(), 1);

    }

    @Test
    @DisplayName(value = "Load exchange rates")
    public void loadExchangeRates() throws DataFeedException {

        ReflectionTestUtils.setField(bundesBankDataFeed,
                "dataFeedLink", "https://www.bundesbank.de/dynamic/action/en/statistics/time-series-databases/time-series-databases/759784/759784?statisticType=BBK_ITS&listId=www_sdks_b01012_3&treeAnchor=WECHSELKURSE");

        Set<ExchangeRate> exchangeRates = new HashSet<>(Arrays.asList(new ExchangeRate(1l, Currency.getInstance("EUR"), Currency.getInstance("INR"), BigDecimal.valueOf(83.9875), new Date(2022,03,18), "")));

        ReflectionTestUtils.setField(bundesBankDataFeed,
                "exchangeRates", exchangeRates);

        Mockito.doNothing().when(dataFeedUtils).parseExchangeRatesForBundesBank("https://www.bundesbank.de/dynamic/action/en/statistics/time-series-databases/time-series-databases/759784/759784?statisticType=BBK_ITS&listId=www_sdks_b01012_3&treeAnchor=WECHSELKURSE",exchangeRates);

        //Check for size.
        Assertions.assertEquals(bundesBankDataFeed.loadExchangeRates().size(), 1);


    }

    @Test
    @DisplayName(value = "Load exchange rates when DataFeedException")
    public void loadExchangeRateDataFeedException() throws DataFeedException {

        ReflectionTestUtils.setField(bundesBankDataFeed,
                "dataFeedLink", "https://www.bundesbank.de/dynamic/action/en/statistics/time-series-databases/time-series-databases/759784/759784?statisticType=BBK_ITS&listId=www_sdks_b01012_3&treeAnchor=WECHSELKURSE");

        Mockito.doThrow(new DataFeedException("")).when(dataFeedUtils).parseExchangeRatesForBundesBank(Mockito.any(), Mockito.anySet());

        Assertions.assertEquals(bundesBankDataFeed.loadExchangeRates().size(), 0);

    }






}
