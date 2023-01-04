package com.github.lexakimov;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author akimov
 * created at: 04.01.2023 07:45
 */
public class PaymentManager {

    private final BigDecimal initialAccountBalance;
    private final TreeMap<LocalDate, BigDecimal> payments = new TreeMap<>();

    public PaymentManager() {
        this(BigDecimal.ZERO);
    }

    public PaymentManager(BigDecimal initialAccountBalance) {
        this.initialAccountBalance = initialAccountBalance;
    }

    public void addPayment(LocalDate date, BigDecimal rate) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(rate);
        payments.put(date, rate);
    }

    public BigDecimal getInitialAccountBalance() {
        return initialAccountBalance;
    }
}
