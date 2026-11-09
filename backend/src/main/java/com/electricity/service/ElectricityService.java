package com.electricity.service;

import com.electricity.model.MeterReading;
import com.electricity.repository.MeterReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class ElectricityService {
    private final MeterReadingRepository repository;
    private static final double FREE_UNIT_LIMIT = 200.0;

    public ElectricityService(MeterReadingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MeterReading addReading(MeterReading reading) {
        if (reading.getReadingDate() == null || reading.getMeterReading() < 0) {
            throw new IllegalArgumentException("Enter a valid date and non-negative meter reading.");
        }
        if (repository.findTopByReadingDateLessThanEqualOrderByReadingDateDesc(reading.getReadingDate()).isPresent()
                && repository.findByReadingDateBetweenOrderByReadingDateAsc(reading.getReadingDate(), reading.getReadingDate()).size() > 0) {
            throw new IllegalArgumentException("A reading already exists for this date.");
        }

        Optional<MeterReading> previous = repository.findTopByReadingDateBeforeOrderByReadingDateDesc(reading.getReadingDate());
        Optional<MeterReading> next = repository.findAllByOrderByReadingDateAsc().stream()
                .filter(r -> r.getReadingDate().isAfter(reading.getReadingDate())).findFirst();

        if (previous.isPresent() && reading.getMeterReading() < previous.get().getMeterReading()) {
            throw new IllegalArgumentException("Meter reading cannot be lower than the previous reading.");
        }
        if (next.isPresent() && reading.getMeterReading() > next.get().getMeterReading()) {
            throw new IllegalArgumentException("Meter reading cannot be higher than the next reading.");
        }

        reading.setUnitsConsumed(previous.map(p -> reading.getMeterReading() - p.getMeterReading()).orElse(0.0));
        MeterReading saved = repository.save(reading);

        // Recalculate the next reading's consumption if a reading was inserted between existing readings.
        if (next.isPresent()) {
            MeterReading nextReading = next.get();
            nextReading.setUnitsConsumed(nextReading.getMeterReading() - reading.getMeterReading());
            repository.save(nextReading);
        }
        return saved;
    }

    public List<MeterReading> getHistory() {
        return repository.findAllByOrderByReadingDateAsc();
    }

    public Map<String, Object> getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        Optional<MeterReading> baseline = repository.findTopByReadingDateLessThanEqualOrderByReadingDateDesc(monthStart.minusDays(1));
        List<MeterReading> monthReadings = repository.findByReadingDateBetweenOrderByReadingDateAsc(monthStart, today);

        double monthUnits = 0;
        double previousValue = baseline.map(MeterReading::getMeterReading).orElse(Double.NaN);
        for (MeterReading r : monthReadings) {
            if (!Double.isNaN(previousValue)) monthUnits += r.getMeterReading() - previousValue;
            previousValue = r.getMeterReading();
        }

        int elapsedDays = today.getDayOfMonth();
        int daysInMonth = today.lengthOfMonth();
        double predictedUnits = elapsedDays > 0 ? monthUnits / elapsedDays * daysInMonth : 0;
        double remaining = Math.max(0, FREE_UNIT_LIMIT - predictedUnits);

        // Illustrative estimate only; configure actual sanctioned tariff and scheme eligibility before real use.
        double illustrativeBill = predictedUnits <= FREE_UNIT_LIMIT ? 0 : (predictedUnits - FREE_UNIT_LIMIT) * 5.0;
        String status = predictedUnits > FREE_UNIT_LIMIT ? "Predicted above 200 units"
                : predictedUnits >= 180 ? "Approaching 200 units" : "Within 200 units";

        List<String> tips = new ArrayList<>();
        if (predictedUnits >= 180) tips.add("Your forecast is close to 200 units. Reduce avoidable appliance usage.");
        if (predictedUnits > 200) tips.add("Your forecast is above 200 units. Check high-consumption appliances.");
        if (tips.isEmpty()) tips.add("Usage is currently below the warning threshold. Keep monitoring your readings.");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("month", today.getMonth().toString());
        result.put("monthlyUnits", round(monthUnits));
        result.put("predictedUnits", round(predictedUnits));
        result.put("remainingUnits", round(remaining));
        result.put("estimatedBill", round(illustrativeBill));
        result.put("status", status);
        result.put("recommendations", tips);
        result.put("freeUnitLimit", FREE_UNIT_LIMIT);
        result.put("hasBaseline", baseline.isPresent() || !monthReadings.isEmpty() && monthReadings.size() > 1);
        return result;
    }

    private double round(double value) { return Math.round(value * 100.0) / 100.0; }
}
