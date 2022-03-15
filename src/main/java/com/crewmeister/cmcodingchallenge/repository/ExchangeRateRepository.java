package com.crewmeister.cmcodingchallenge.repository;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Currency;
import java.util.Date;
import java.util.List;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    List<ExchangeRate> findAllByDate(Date date);

    List<ExchangeRate> findAllByDateAndCurTo(Date date, Currency currency);

    List<ExchangeRate> findAllByDateAndCurFromAndCurTo(Date date, Currency curFrom, Currency curTo);
}
