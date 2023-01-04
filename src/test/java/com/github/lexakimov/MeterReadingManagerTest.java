package com.github.lexakimov;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class MeterReadingManagerTest {

    @Test
    void interpolationTest() {
        var meterReadingManager = new MeterReadingManager();

        meterReadingManager.enterMeterReading(LocalDate.of(2023, 1, 1), 0);
        meterReadingManager.enterMeterReading(LocalDate.of(2023, 2, 1), 31);

        var reading1 = meterReadingManager.getMeterReadingFor(LocalDate.of(2023, 1, 14), true);
        assertThat(reading1, equalTo(13));

        var reading2 = meterReadingManager.getMeterReadingFor(LocalDate.of(2023, 1, 15), true);
        assertThat(reading2, equalTo(14));

        var reading3 = meterReadingManager.getMeterReadingFor(LocalDate.of(2023, 1, 16), true);
        assertThat(reading3, equalTo(15));
    }
}