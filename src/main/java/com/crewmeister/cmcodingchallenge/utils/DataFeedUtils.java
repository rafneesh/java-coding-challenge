package com.crewmeister.cmcodingchallenge.utils;

import com.crewmeister.cmcodingchallenge.entity.CurrencyHolder;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
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
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class DataFeedUtils {

    @Autowired
    CurrencyHolder baseCurrency;

    public void parseCurrencyForBundesBank(String dataFeedLink, Set<CurrencyHolder> currenciesSet) throws Exception {

        // Here we create a document object and use JSoup to fetch the website
        Document doc = Jsoup.connect(dataFeedLink).get();

        doc.select("table tbody tr").stream().forEach((element -> parseCurrencyForBundesBankTableElement(element, currenciesSet)));
    }

    private void parseCurrencyForBundesBankTableElement(Element element, Set<CurrencyHolder> currencyHolderSet) {

        Elements tds = element.select("td");

        if (tds.size() > 0) {

            Element secondTd = tds.get(1);

            String currencyName = secondTd.text().split("=")[1].split("... /")[0].trim();

            try {

                Currency currency = Currency.getInstance(currencyName.toUpperCase(Locale.ROOT));

                currencyHolderSet.add(new CurrencyHolder(currency));

            } catch (IllegalArgumentException e) {

                log.debug("Inside parseCurrencyForBundesBankTableElement currency not found " + e);
            }

        }

    }

    public void parseExchangeRatesForBundesBank(String dataFeedLink, Set<ExchangeRate> exchangeRateSet) throws Exception {

        // Here we create a document object and use JSoup to fetch the website
        Document doc = Jsoup.connect(dataFeedLink).get();

        doc.select("table tbody tr").stream().forEach((element -> parseExchangeRatesForForBundesBankTableElement(element, exchangeRateSet)));
    }

    private void parseExchangeRatesForForBundesBankTableElement(Element element, Set<ExchangeRate> exchangeRateSet) {

        Elements tds = element.select("td");

        if (tds.size() > 0) {

            Element downloadCsvEle = tds.get(2).getElementsByAttribute("href").get(0);

            String downloadCsvLink = downloadCsvEle.attr("href");

            log.debug("Link to Exchange Rate:" + downloadCsvLink);

            URL url = null;

            try {

                url = new URL(downloadCsvLink);

            } catch (MalformedURLException e) {

                log.debug("Inside parseExchangeRatesForForBundesBankTableElement CSV file URL is malformed/not found " + e);
            }

            try (

                    BufferedReader read = new BufferedReader(
                            new InputStreamReader(url.openStream()));
                    CSVParser csvParser = new CSVParser(read, CSVFormat.DEFAULT);

            ) {

                Currency currTo = null;
                Currency currFrom = null;

                for (CSVRecord csvRecord : csvParser) {
                    // Accessing Values by Column Index

                    //Assumed that the currency name is in the 7th row, 2nd column and the file is for the base currency configured in app.properties
                    if (csvParser.getRecordNumber() == 7) {
                        try {
                            currTo = baseCurrency.getCurrency();
                            currFrom = Currency.getInstance(csvRecord.get(1).toUpperCase(Locale.ROOT));
                        } catch (Exception e) {
                            log.warn("File is invalid for its currency name, loadExchangeRates()" + e);
                            break;
                        }

                    }

                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date date = null;
                    BigDecimal rate = null;
                    String remarks = null;

                    try {

                        date = formatter.parse(csvRecord.get(0));
                        rate = BigDecimal.valueOf(Double.valueOf(csvRecord.get(1)));
                        remarks = csvRecord.get(2);

                        ExchangeRate exchangeRate = new ExchangeRate();
                        exchangeRate.setCurFrom(currFrom);
                        exchangeRate.setCurTo(currTo);

                        exchangeRate.setRate(rate);
                        exchangeRate.setDate(date);
                        exchangeRate.setRemark(remarks);

                        exchangeRateSet.add(exchangeRate);


                    } catch (ParseException | NumberFormatException e) {

                        log.debug("Fail to parse " + e);

                    }

                }

            } catch (Exception e) {
                log.warn("Exception in file iteration loadExchangeRates() " + e);
            }
        }
    }


}
