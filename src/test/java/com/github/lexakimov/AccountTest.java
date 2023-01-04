package com.github.lexakimov;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

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

        var billPositions = account.getPositions();
        assertThat(billPositions, hasSize(2));

        {
            AccountPosition accountPosition = billPositions.get(0);
            assertThat(accountPosition.date(), equalTo(accountOpeningDate));
            assertThat(accountPosition.meterReading(), equalTo(0));
            assertThat(accountPosition.consumptionVolume(), equalTo(0));
            assertThat(accountPosition.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(accountPosition.cost(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(accountPosition.accountBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(accountPosition.accountDebt(), comparesEqualTo(BigDecimal.ZERO));
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

        var billPositions = account.getPositions();
        assertThat(billPositions, hasSize(3));

        {
            AccountPosition accountPosition = billPositions.get(0);
            assertThat(accountPosition.date(), equalTo(accountOpeningDate));
            assertThat(accountPosition.meterReading(), equalTo(0));
            assertThat(accountPosition.consumptionVolume(), equalTo(0));
            assertThat(accountPosition.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(accountPosition.cost(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(accountPosition.accountBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(accountPosition.accountDebt(), comparesEqualTo(BigDecimal.ZERO));
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

    @Test
    void calculateFromScratchWithPayment_goodCase() {
        var accountOpeningDate = LocalDate.of(2023, 1, 1);

        var rateManager = new RateManager();
        var paymentManager = new PaymentManager();
        var meterReadingManager = new MeterReadingManager();
        var account = new Account(accountOpeningDate, rateManager, paymentManager, meterReadingManager);

        rateManager.addRate(LocalDate.of(2023, 1, 1), BigDecimal.TEN);
        meterReadingManager.enterMeterReading(LocalDate.of(2023, 2, 1), 10);
        meterReadingManager.enterMeterReading(LocalDate.of(2023, 3, 1), 20);
        paymentManager.addPayment(LocalDate.of(2023, 2, 1), BigDecimal.valueOf(70));

        var settlementDate = LocalDate.of(2023, 3, 1);
        account.calculateUntil(settlementDate);

        var positions = account.getPositions();
        assertThat(positions, hasSize(4));

        {
            var position = positions.get(0);
            assertThat(position.date(), equalTo(accountOpeningDate));
            assertThat(position.type(), equalTo(AccountPositionType.OPENING_AN_ACCOUNT));

            assertThat(position.meterReading(), equalTo(0));
            assertThat(position.consumptionVolume(), equalTo(0));
            assertThat(position.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(position.cost(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(position.accountBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(position.accountDebt(), comparesEqualTo(BigDecimal.ZERO));
        }

        {
            var position = positions.get(1);
            assertThat(position.date(), equalTo(LocalDate.of(2023, 2, 1)));
            assertThat(position.type(), equalTo(AccountPositionType.ENTERING_METER_READINGS));

            assertThat(position.meterReading(), equalTo(10));
            assertThat(position.consumptionVolume(), equalTo(10));
            assertThat(position.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(position.cost(), comparesEqualTo(BigDecimal.valueOf(100)));
            assertThat(position.accountBalance(), comparesEqualTo(BigDecimal.ZERO));
            assertThat(position.accountDebt(), comparesEqualTo(BigDecimal.valueOf(-100)));
        }

        {
            var position = positions.get(2);
            assertThat(position.date(), equalTo(LocalDate.of(2023, 2, 1)));
            assertThat(position.type(), equalTo(AccountPositionType.PAYMENT));

            assertThat(position.meterReading(), nullValue());
            assertThat(position.consumptionVolume(), nullValue());
            assertThat(position.rate(), nullValue());
            assertThat(position.cost(), comparesEqualTo(BigDecimal.valueOf(-70)));
            assertThat(position.accountBalance(), comparesEqualTo(BigDecimal.valueOf(-100)));
            assertThat(position.accountDebt(), comparesEqualTo(BigDecimal.valueOf(-30)));
        }

        {
            var position = positions.get(3);
            assertThat(position.date(), equalTo(settlementDate));
            assertThat(position.type(), equalTo(AccountPositionType.ENTERING_METER_READINGS));

            assertThat(position.meterReading(), equalTo(20));
            assertThat(position.consumptionVolume(), equalTo(10));
            assertThat(position.rate(), comparesEqualTo(BigDecimal.TEN));
            assertThat(position.cost(), comparesEqualTo(BigDecimal.valueOf(100)));
            assertThat(position.accountBalance(), comparesEqualTo(BigDecimal.valueOf(-30)));
            assertThat(position.accountDebt(), comparesEqualTo(BigDecimal.valueOf(-130)));
        }
    }

}