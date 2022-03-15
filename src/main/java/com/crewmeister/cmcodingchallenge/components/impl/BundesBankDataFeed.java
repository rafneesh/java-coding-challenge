package com.crewmeister.cmcodingchallenge.components.impl;

import com.crewmeister.cmcodingchallenge.components.DataFeed;
import com.crewmeister.cmcodingchallenge.config.Config;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class BundesBankDataFeed implements DataFeed {

    @Autowired
    Config config;

    @Value("${datafeed.bundesbank}")
    String link;

    Set<CurrencyHolder> currencies = new HashSet<>();

    Set<ExchangeRate> exchangeRates = new HashSet<>();

    @Override
    public Set<CurrencyHolder> loadCurrencies() {

        log.info("Loading currencies Starts =>" + link);

        //Add the base currency if configured
        currencies.add(config.getBaseCurrencyHolder());

        try {

            // Here we create a document object and use JSoup to fetch the website
            Document doc = Jsoup.connect(link).get();

            Elements tableElements = doc.select("table tbody tr");

            for (Element tr : tableElements) {

                Elements tds = tr.select("td");

                if (tds.size() > 0) {
                    Element secondTd = tds.get(1);

                    String currencyName = secondTd.text().split("=")[1].split("... /")[0].trim();

                    Currency currency = Currency.getInstance(currencyName);

                    currencies.add(new CurrencyHolder(currency));
                }
            }

            // In case of any IO errors, we want the messages written to the console

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Loading currencies Done");

        return currencies;

    }

    @Override
    public Set<ExchangeRate> loadExchangeRates() {

        log.info("Loading loadExchangeRates Starts");

        try {

            // Here we create a document object and use JSoup to fetch the website
            Document doc = Jsoup.connect(link).get();

            Elements tableElements = doc.select("table tbody tr");

            for (Element tr : tableElements) {

                Elements tds = tr.select("td");

                if (tds.size() > 0) {

                    Element downloadCsvEle = tds.get(2).getElementsByAttribute("href").get(0);

                    String downloadCsvLink = downloadCsvEle.attr("href");

                    log.debug("Link to Exchange Rate:" + downloadCsvLink);

                    URL url = new URL(downloadCsvLink);

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
                                    currTo  = config.getBaseCurrencyHolder().getCurrency();
                                    currFrom = Currency.getInstance(csvRecord.get(1));
                                }catch (Exception e){
                                    log.warn("File is invalid for its currency name, loadExchangeRates()" + e);
                                    break;
                                }

                            }

                            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = null;
                            Double rate = null;
                            String remarks = null;

                            try {


                                date = formatter.parse(csvRecord.get(0));
                                rate = Double.parseDouble(csvRecord.get(1));
                                remarks = csvRecord.get(2);


                                ExchangeRate exchangeRate = new ExchangeRate();
                                exchangeRate.setCurFrom(currFrom);
                                exchangeRate.setCurTo(currTo);

                                exchangeRate.setRate(rate);
                                exchangeRate.setDate(date);
                                exchangeRate.setRemark(remarks);

                                exchangeRates.add(exchangeRate);


                            } catch (ParseException | NumberFormatException e) {

                                //doNothing

                            }

                        }

                    } catch (Exception e) {
                        log.warn("Exception in file iteration loadExchangeRates()" + e);
                    }
                }
            }

        } catch (IOException e) {
            log.warn("IOException in parsing the table loadExchangeRates()" + e);
        }


        log.info("Loading loadExchangeRates Done " + exchangeRates.size());

        return exchangeRates;

    }


}
