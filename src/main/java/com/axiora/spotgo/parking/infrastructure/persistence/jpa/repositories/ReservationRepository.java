package com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByParkingId(String parkingId);
    List<Reservation> findByClientId(String clientId);
}
