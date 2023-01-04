package com.github.lexakimov;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class AccountTest {

    @Test
    void calculateFromScratch_2positions_goodCase() {
        var accountOpeningDate = LocalDate.of(2023, 1, 1);

        var rateManager = new RateManager();
        var paymentManager = new PaymentManager();
        var meterReadingManager = new MeterReadingManager();
        var account = new Account(accountOpeningDate, rateManager, paymentManager, meterReadingManager);

        rateManager.addRate(LocalDate.of(2023, 1, 1), BigDecimal.TEN);
        meterReadingManager.enterMeterReading(LocalDate.of(2023, 2, 1), 10);

        var settlementDate = LocalDate.of(2023, 2, 1);
        account.calculateUntil(settlementDate);

        var billPositions = account.getBillPositions();
        assertThat(billPositions, hasSize(2));

        {
            BillPosition billPosition = billPositions.get(0);
            assertThat(billPosition.date(), equalTo(accountOpeningDate));
            assertThat(billPosition.meterReading(), equalTo(0));
            assertThat(billPosition.consumptionVolume(), equalTo(0));
            assertThat(billPosition.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(billPosition.cost(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(billPosition.accountBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(billPosition.accountDebt(), comparesEqualTo(BigDecimal.ZERO));
        }

        {
            var billPosition = billPositions.get(1);
            assertThat(billPosition.date(), equalTo(settlementDate));
            assertThat(billPosition.meterReading(), equalTo(10));
            assertThat(billPosition.consumptionVolume(), equalTo(10));
            assertThat(billPosition.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(billPosition.cost(), comparesEqualTo(BigDecimal.valueOf(100)));
            assertThat(billPosition.accountBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(billPosition.accountDebt(), comparesEqualTo(BigDecimal.valueOf(-100)));
        }
    }

    @Test
    void calculateFromScratch_3positions_goodCase() {
        var accountOpeningDate = LocalDate.of(2023, 1, 1);

        var rateManager = new RateManager();
        var paymentManager = new PaymentManager();
        var meterReadingManager = new MeterReadingManager();
        var account = new Account(accountOpeningDate, rateManager, paymentManager, meterReadingManager);

        rateManager.addRate(LocalDate.of(2023, 1, 1), BigDecimal.TEN);
        meterReadingManager.enterMeterReading(LocalDate.of(2023, 2, 1), 10);
        meterReadingManager.enterMeterReading(LocalDate.of(2023, 3, 1), 20);

        var settlementDate = LocalDate.of(2023, 3, 1);
        account.calculateUntil(settlementDate);

        var billPositions = account.getBillPositions();
        assertThat(billPositions, hasSize(3));

        {
            BillPosition billPosition = billPositions.get(0);
            assertThat(billPosition.date(), equalTo(accountOpeningDate));
            assertThat(billPosition.meterReading(), equalTo(0));
            assertThat(billPosition.consumptionVolume(), equalTo(0));
            assertThat(billPosition.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(billPosition.cost(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(billPosition.accountBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(billPosition.accountDebt(), comparesEqualTo(BigDecimal.ZERO));
        }

        {
            var billPosition = billPositions.get(1);
            assertThat(billPosition.date(), equalTo(LocalDate.of(2023, 2, 1)));
            assertThat(billPosition.meterReading(), equalTo(10));
            assertThat(billPosition.consumptionVolume(), equalTo(10));
            assertThat(billPosition.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(billPosition.cost(), comparesEqualTo(BigDecimal.valueOf(100)));
            assertThat(billPosition.accountBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(billPosition.accountDebt(), comparesEqualTo(BigDecimal.valueOf(-100)));
        }

        {
            var billPosition = billPositions.get(2);
            assertThat(billPosition.date(), equalTo(settlementDate));
            assertThat(billPosition.meterReading(), equalTo(20));
            assertThat(billPosition.consumptionVolume(), equalTo(10));
            assertThat(billPosition.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(billPosition.cost(), comparesEqualTo(BigDecimal.valueOf(100)));
            assertThat(billPosition.accountBalance(), comparesEqualTo(BigDecimal.valueOf(-100)));
            assertThat(billPosition.accountDebt(), comparesEqualTo(BigDecimal.valueOf(-200)));
        }
    }

}