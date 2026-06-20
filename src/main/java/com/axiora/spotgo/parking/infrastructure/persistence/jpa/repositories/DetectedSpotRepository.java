package com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetectedSpotRepository extends JpaRepository<DetectedSpot, Long> {
    List<DetectedSpot> findByBlueprintId(Long blueprintId);

    @Query("SELECT d FROM DetectedSpot d WHERE d.blueprintId IN (SELECT b.id FROM Blueprint b WHERE b.parkingId = :parkingId)")
    List<DetectedSpot> findByParkingId(@Param("parkingId") Long parkingId);
}
