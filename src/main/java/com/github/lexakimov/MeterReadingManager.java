package com.github.lexakimov;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author akimov
 * created at: 04.01.2023 08:06
 */
public class MeterReadingManager {

    private final TreeMap<LocalDate, Integer> meterReadings = new TreeMap<>();

    private final Integer initialMeterReading;

    public MeterReadingManager() {
        this(0);
    }

    public MeterReadingManager(Integer initialMeterReading) {
        Objects.requireNonNull(initialMeterReading);
        this.initialMeterReading = initialMeterReading;
    }

    public void enterMeterReading(LocalDate date, Integer meterReading) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(meterReading);
        meterReadings.put(date, meterReading);
    }

    public Integer getInitialMeterReading() {
        return initialMeterReading;
    }

    public Integer getMeterReadingFor(LocalDate date) {
        return meterReadings.get(date);
    }

    public Integer getMeterReadingFor(LocalDate date, boolean interpolate) {
        var reading = meterReadings.get(date);

        if (reading == null && interpolate) {
            var floorEntry = meterReadings.floorEntry(date);
            var floorDate = floorEntry.getKey();
            var floorMeasurement = floorEntry.getValue();

            var ceilingEntry = meterReadings.ceilingEntry(date);
            var ceilingDate = ceilingEntry.getKey();
            var ceilingMeasurement = ceilingEntry.getValue();

            var daysRange = (int) ChronoUnit.DAYS.between(floorDate, ceilingDate);
            var daysPassed = (int) ChronoUnit.DAYS.between(floorDate, date);

            var interpolated =
                    floorMeasurement + ((double) ceilingMeasurement - floorMeasurement) / daysRange * daysPassed;
            reading = (int) interpolated;
        }

        return reading;
    }
}
