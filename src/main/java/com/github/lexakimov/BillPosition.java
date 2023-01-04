package com.github.lexakimov;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @param date
 * @param meterReading
 * @param interpolated      does this <code>meterReading</code> was interpolated?
 * @param consumptionVolume difference between current and previous <code>meterReading</code>.
 * @param rate              price per consumed unit.
 * @param cost              a product of <code>consumptionVolume</code> and <code>rate</code>.
 * @param accountBalance    an account balance before subtraction of <code>cost</code>.
 * @param accountDebt       an account balance after subtraction of <code>cost</code>.
 * @author akimov
 * created at: 04.01.2023 07:43
 */
public record BillPosition(
        LocalDate date,
        Integer meterReading,
        boolean interpolated,

        Integer consumptionVolume,
        BigDecimal rate,
        BigDecimal cost,
        BigDecimal accountBalance,
        BigDecimal accountDebt
) {
}
