package com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, String> {
    Optional<Parking> findByAdminId(String adminId);
}
