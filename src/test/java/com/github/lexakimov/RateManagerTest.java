package com.github.lexakimov;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RateManagerTest {

    @Test
    void getRateSuccess() {
        var rateManager = new RateManager();
        rateManager.addRate(LocalDate.of(2023, 1, 3), BigDecimal.ONE);
        rateManager.addRate(LocalDate.of(2023, 1, 5), BigDecimal.TEN);

        assertThat(rateManager.getRateFor(LocalDate.of(2023, 1, 3)), comparesEqualTo(BigDecimal.ONE));
        assertThat(rateManager.getRateFor(LocalDate.of(2023, 1, 4)), comparesEqualTo(BigDecimal.ONE));
        assertThat(rateManager.getRateFor(LocalDate.of(2023, 1, 5)), comparesEqualTo(BigDecimal.TEN));
    }

    @Test
    void getRateFail() {
        var rateManager = new RateManager();
        rateManager.addRate(LocalDate.of(2023, 1, 3), BigDecimal.ONE);
        rateManager.addRate(LocalDate.of(2023, 1, 5), BigDecimal.TEN);

        var date = LocalDate.of(2023, 1, 2);
        var exception = assertThrows(NoSuchElementException.class, () -> rateManager.getRateFor(date));
        assertThat(exception.getMessage(), Matchers.equalTo("Rate for date 2023-01-02 not found"));
    }
}