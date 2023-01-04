package com.github.lexakimov;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author akimov
 * created at: 04.01.2023 07:23
 */
public class RateManager {

    private final TreeMap<LocalDate, BigDecimal> rates = new TreeMap<>();

    /**
     * @param date start date of the rate.
     * @param rate cost per month.
     */
    public void addRate(LocalDate date, BigDecimal rate) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(rate);
        rates.put(date, rate);
    }

    /**
     * @return the actual rate for specified date.
     * @throws java.util.NoSuchElementException if there is no rate for specified date.
     */
    public BigDecimal getRateFor(LocalDate date) {
        return Optional.ofNullable(rates.floorEntry(date))
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new NoSuchElementException("Rate for date %s not found".formatted(date)));
    }
}
