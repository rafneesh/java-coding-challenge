package com.crewmeister.cmcodingchallenge.utils;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import com.crewmeister.cmcodingchallenge.exception.DataFeedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

@Component
@Slf4j
public class DataFeedUtils {

    private static final String CSSQUERY_TABLE_ROW = "table tbody tr";

    private static final String CSSQUERY_TABLE_CELL = "td";

    private static final String CSSQUERY_LINK = "href";

    @Autowired
    CurrencyHolder baseCurrency;

    public void parseCurrencyForBundesBank(String dataFeedLink, Set<CurrencyHolder> currenciesSet) throws DataFeedException {

        // Here we create a document object and use JSoup to fetch the website
        Document doc = connectBundesBank(dataFeedLink);

        doc.select(CSSQUERY_TABLE_ROW).stream().forEach((element -> parseCurrencyForBundesBankTableElement(element, currenciesSet)));
    }

    public void parseExchangeRatesForBundesBank(String dataFeedLink, Set<ExchangeRate> exchangeRateSet) throws DataFeedException {

        // Here we create a document object and use JSoup to fetch the website
        Document doc = connectBundesBank(dataFeedLink);

        doc.select(CSSQUERY_TABLE_ROW).stream().forEach((element -> parseExchangeRatesForForBundesBankTableElement(element, exchangeRateSet)));
    }

    private Document connectBundesBank(String dataFeedLink) throws DataFeedException {

        // Here we create a document object and use JSoup to fetch the website
        Document doc = null;
        try {

            doc = Jsoup.connect(dataFeedLink).get();

        } catch (IOException e) {

            log.error("Error in connecting webpage " + e);
            throw new DataFeedException(e.getMessage());
        }

        return doc;

    }

    private void parseCurrencyForBundesBankTableElement(Element element, Set<CurrencyHolder> currencyHolderSet) {

        Elements tds = element.select(CSSQUERY_TABLE_CELL);

        if (tds.isEmpty())
            return;

        Element secondTd = tds.get(1);

        String currencyName = secondTd.text().split("=")[1].split("... /")[0].trim();

        try {

            Currency currency = Currency.getInstance(currencyName.toUpperCase(Locale.ROOT));

            currencyHolderSet.add(new CurrencyHolder(currency));

        } catch (IllegalArgumentException e) {

            log.debug("Inside parseCurrencyForBundesBankTableElement currency not found " + e);
        }


    }


    private void parseExchangeRatesForForBundesBankTableElement(Element element, Set<ExchangeRate> exchangeRateSet) {

        Elements tds = element.select(CSSQUERY_TABLE_CELL);

        if (tds.isEmpty())
            return;


        Element downloadCsvEle = tds.get(2).getElementsByAttribute(CSSQUERY_LINK).get(0);

        String downloadCsvLink = downloadCsvEle.attr(CSSQUERY_LINK);

        log.debug("Link to Exchange Rate:" + downloadCsvLink);

        URL url = null;

        try {

            url = new URL(downloadCsvLink);

        } catch (MalformedURLException e) {

            log.debug("Inside parseExchangeRatesForForBundesBankTableElement CSV file URL is malformed/not found " + e);
            return;
        }

        try (

                BufferedReader read = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                CSVParser csvParser = new CSVParser(read, CSVFormat.DEFAULT);

        ) {

            Currency currTo = null;

            for (var csvRecord : csvParser) {
                // Accessing Values by Column Index

                //Assumed that the currency name is in the 7th row, 2nd column and the file is for the base currency configured in app.properties
                if (csvParser.getRecordNumber() == 7) {

                    currTo = getCurrencyOfFile(csvRecord);

                }

                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                buildAndAddExchangeRate(csvRecord, formatter, currTo, exchangeRateSet);

            }

        } catch (Exception e) {
            log.warn("Exception in file iteration loadExchangeRates() " + e);

        }

    }

    private Currency getCurrencyOfFile(CSVRecord csvRecord) {

        try {
            return Currency.getInstance(csvRecord.get(1).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.warn("File is invalid for its currency name, loadExchangeRates()" + e);
        }

        return null;
    }

    private void buildAndAddExchangeRate(CSVRecord csvRecord, DateFormat formatter, Currency currTo, Set<ExchangeRate> exchangeRateSet) {

        try {

            Date date = formatter.parse(csvRecord.get(0));
            BigDecimal rate = BigDecimal.valueOf(Double.valueOf(csvRecord.get(1)));
            String remarks = csvRecord.get(2);

            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setCurFrom(baseCurrency.getCurrency());
            exchangeRate.setCurTo(currTo);

            exchangeRate.setRate(rate);
            exchangeRate.setDate(date);
            exchangeRate.setRemark(remarks);

            exchangeRateSet.add(exchangeRate);


        } catch (ParseException | NumberFormatException e) {

            log.debug("Fail to parse " + e);

        }
    }


}
