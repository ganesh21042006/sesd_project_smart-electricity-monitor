package com.electricity.repository;

import com.electricity.model.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    List<MeterReading> findAllByOrderByReadingDateAsc();
    Optional<MeterReading> findTopByReadingDateBeforeOrderByReadingDateDesc(LocalDate date);
    List<MeterReading> findByReadingDateBetweenOrderByReadingDateAsc(LocalDate start, LocalDate end);
    Optional<MeterReading> findTopByReadingDateLessThanEqualOrderByReadingDateDesc(LocalDate date);
}
