package com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetectedSpotRepository extends JpaRepository<DetectedSpot, Long> {
    List<DetectedSpot> findByBlueprintId(Long blueprintId);
}
