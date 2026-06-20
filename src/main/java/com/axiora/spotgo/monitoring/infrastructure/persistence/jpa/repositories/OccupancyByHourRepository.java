package com.axiora.spotgo.monitoring.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.monitoring.domain.model.aggregates.OccupancyByHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OccupancyByHourRepository extends JpaRepository<OccupancyByHour, Long> {
    List<OccupancyByHour> findByParkingId(Long parkingId);
}
