package com.github.lexakimov;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import static com.github.lexakimov.AccountPositionType.ENTERING_METER_READINGS;
import static com.github.lexakimov.AccountPositionType.OPENING_AN_ACCOUNT;

/**
 * @author akimov
 * created at: 04.01.2023 07:22
 */
public class Account {

    private final TreeMap<LocalDate, AccountPosition> positions = new TreeMap<>();

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

        AccountPosition position = null;

        for (LocalDate date : dates) {
            position = (position == null) ? calcInitialPosition(date) : calcPosition(date, position);
            positions.put(date, position);
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

    private AccountPosition calcInitialPosition(LocalDate date) {
        var rate = rateManager.getRateFor(date);
        var balance = paymentManager.getInitialAccountBalance();
        var meterReading = meterReadingManager.getInitialMeterReading();
        var consumed = 0;
        var cost = BigDecimal.ZERO;
        var debt = BigDecimal.ZERO;

        return new AccountPosition(date, meterReading, false, OPENING_AN_ACCOUNT, consumed, rate, cost, balance, debt);
    }

    private AccountPosition calcPosition(LocalDate date, AccountPosition prevPosition) {
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

        return new AccountPosition
                (date, meterReading, isInterpolated, ENTERING_METER_READINGS, consumed, rate, cost, balance, debt);
    }

    public List<AccountPosition> getBillPositions() {
        return positions.values().stream().toList();
    }
}
