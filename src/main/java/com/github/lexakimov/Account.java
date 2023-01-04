package com.github.lexakimov;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import static java.math.BigDecimal.ZERO;

/**
 * @author akimov
 * created at: 04.01.2023 07:22
 */
public class Account {

    private final TreeMap<LocalDate, BillPosition> results = new TreeMap<>();

    private final LocalDate accountOpeningDate;
    private final RateManager rateManager;
    private final PaymentManager paymentManager;
    private final MeterReadingManager meterReadingManager;

    public Account(
            LocalDate accountOpeningDate,
            RateManager rateManager,
            PaymentManager paymentManager,
            MeterReadingManager meterReadingManager
    ) {
        Objects.requireNonNull(accountOpeningDate);
        Objects.requireNonNull(rateManager);
        Objects.requireNonNull(paymentManager);
        Objects.requireNonNull(meterReadingManager);
        this.accountOpeningDate = accountOpeningDate;
        this.rateManager = rateManager;
        this.paymentManager = paymentManager;
        this.meterReadingManager = meterReadingManager;
    }

    /**
     * @param date exclusive
     */
    public void calculateUntil(LocalDate date) {
        var initialRate = rateManager.getRateFor(accountOpeningDate);
        var initialBalance = paymentManager.getInitialAccountBalance();
        var initialMeterReading = meterReadingManager.getInitialMeterReading();
        var initialPosition = new BillPosition(
                accountOpeningDate,
                initialMeterReading,
                false,
                0,
                initialRate,
                ZERO,
                initialBalance,
                ZERO);
        results.put(accountOpeningDate, initialPosition);


        var rate = rateManager.getRateFor(date);
        var balance = initialPosition.accountDebt();

        boolean isInterpolated = false;
        var meterReading = meterReadingManager.getMeterReadingFor(date);
        if (meterReading == null) {
            isInterpolated = true;
            meterReading = meterReadingManager.getMeterReadingFor(date, true);
        }
        var consumed = meterReading - initialMeterReading;
        var cost = rate.multiply(BigDecimal.valueOf(consumed));
        var debt = balance.subtract(cost);

        var position = new BillPosition(date, meterReading, isInterpolated, consumed, rate, cost, balance, debt);
        results.put(date, position);
    }

    public List<BillPosition> getBillPositions() {
        return results.values().stream().toList();
    }
}
