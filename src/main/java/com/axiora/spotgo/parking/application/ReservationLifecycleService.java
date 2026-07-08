package com.axiora.spotgo.parking.application;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReservationStatus;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ReservationRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationLifecycleService {

    private final ReservationRepository reservationRepository;
    private final ParkingOccupancyService parkingOccupancyService;
    private final Clock clock;

    public ReservationLifecycleService(ReservationRepository reservationRepository,
                                       ParkingOccupancyService parkingOccupancyService,
                                       Clock clock) {
        this.reservationRepository = reservationRepository;
        this.parkingOccupancyService = parkingOccupancyService;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${app.reservations.lifecycle.fixed-delay-ms:60000}")
    @Transactional
    public void reconcileExpiredReservations() {
        var now = LocalDateTime.now(clock);
        var expiredReservations = reservationRepository.findByStatusAndEndDateBefore(ReservationStatus.ACTIVE, now);
        if (expiredReservations.isEmpty()) {
            return;
        }

        var affectedParkingIds = new java.util.HashSet<String>();
        for (var reservation : expiredReservations) {
            reservation.updateStatus(ReservationStatus.COMPLETED);
            affectedParkingIds.add(reservation.getParkingId());
        }
        reservationRepository.saveAll(expiredReservations);

        for (var parkingId : affectedParkingIds) {
            parkingOccupancyService.reconcileParking(parkingId, now);
        }
    }
}
