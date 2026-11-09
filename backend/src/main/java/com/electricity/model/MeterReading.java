package com.electricity.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "meter_readings")
public class MeterReading {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private LocalDate readingDate;
    @Column(nullable = false)
    private double meterReading;
    @Column(nullable = false)
    private double unitsConsumed;

    public Long getId() { return id; }
    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }
    public double getMeterReading() { return meterReading; }
    public void setMeterReading(double meterReading) { this.meterReading = meterReading; }
    public double getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(double unitsConsumed) { this.unitsConsumed = unitsConsumed; }
}
