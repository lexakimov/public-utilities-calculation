package com.github.lexakimov;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

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
     * @param settlementDate inclusive
     */
    public void calculateUntil(LocalDate settlementDate) {
        List<LocalDate> dates = getMilestones(settlementDate);

        BillPosition position = null;

        for (LocalDate date : dates) {
            position = (position == null) ? calcInitialPosition(date) : calcPosition(date, position);
            results.put(date, position);
        }
    }

    private List<LocalDate> getMilestones(LocalDate date) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(accountOpeningDate);

        var monthBetween = ChronoUnit.MONTHS.between(accountOpeningDate, date);
        var currentDate = accountOpeningDate.withDayOfMonth(1);
        for (long i = 0; i < monthBetween; i++) {
            currentDate = currentDate.plusMonths(1);
            dates.add(currentDate);
        }
        if (!date.isEqual(currentDate)) {
            dates.add(date);
        }

        return dates;
    }

    private BillPosition calcInitialPosition(LocalDate date) {
        var rate = rateManager.getRateFor(date);
        var balance = paymentManager.getInitialAccountBalance();
        var meterReading = meterReadingManager.getInitialMeterReading();
        var consumed = 0;
        var cost = BigDecimal.ZERO;
        var debt = BigDecimal.ZERO;

        return new BillPosition(date, meterReading, false, consumed, rate, cost, balance, debt);
    }

    private BillPosition calcPosition(LocalDate date, BillPosition prevPosition) {
        var rate = rateManager.getRateFor(date);
        var balance = prevPosition.accountDebt();
        var isInterpolated = false;
        var meterReading = meterReadingManager.getMeterReadingFor(date);
        if (meterReading == null) {
            isInterpolated = true;
            meterReading = meterReadingManager.getMeterReadingFor(date, true);
        }
        var consumed = meterReading - prevPosition.meterReading();
        var cost = rate.multiply(BigDecimal.valueOf(consumed));
        var debt = balance.subtract(cost);

        return new BillPosition(date, meterReading, isInterpolated, consumed, rate, cost, balance, debt);
    }

    public List<BillPosition> getBillPositions() {
        return results.values().stream().toList();
    }
}
