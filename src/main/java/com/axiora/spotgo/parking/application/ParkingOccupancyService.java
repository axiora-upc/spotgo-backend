package com.axiora.spotgo.parking.application;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReservationStatus;
import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.DetectedSpotRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ReservationRepository;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParkingOccupancyService {

    private final ReservationRepository reservationRepository;
    private final DetectedSpotRepository detectedSpotRepository;
    private final ParkingRepository parkingRepository;

    public ParkingOccupancyService(ReservationRepository reservationRepository,
                                   DetectedSpotRepository detectedSpotRepository,
                                   ParkingRepository parkingRepository) {
        this.reservationRepository = reservationRepository;
        this.detectedSpotRepository = detectedSpotRepository;
        this.parkingRepository = parkingRepository;
    }

    @Transactional
    public void reconcileParking(String parkingId, LocalDateTime now) {
        var occupiedSpotIds = reservationRepository.findByParkingIdAndStatus(parkingId, ReservationStatus.ACTIVE).stream()
                .filter(reservation -> !reservation.getStartDate().isAfter(now) && reservation.getEndDate().isAfter(now))
                .map(reservation -> reservation.getSpot())
                .collect(Collectors.toSet());

        var detectedSpots = detectedSpotRepository.findByParkingId(parkingId);
        boolean spotsChanged = false;

        for (var spot : detectedSpots) {
            if (spot.getRow() == null || spot.getCol() == null) {
                continue;
            }
            if (spot.getStatus() == SpotStatus.MAINTENANCE) {
                continue;
            }

            var shouldBeOccupied = occupiedSpotIds.contains(toSpotId(spot.getRow(), spot.getCol()));
            var nextStatus = shouldBeOccupied ? SpotStatus.OCCUPIED : SpotStatus.FREE;
            if (spot.getStatus() != nextStatus) {
                spot.updateStatus(nextStatus);
                spotsChanged = true;
            }
        }

        if (spotsChanged) {
            detectedSpotRepository.saveAll(detectedSpots);
        }

        parkingRepository.findById(parkingId).ifPresent(parking -> {
            int availableSpaces = computeAvailableSpaces(parkingId, detectedSpots, occupiedSpotIds);
            parking.updateStats(null, availableSpaces, null, null);
            parkingRepository.save(parking);
        });
    }

    private int computeAvailableSpaces(String parkingId,
                                       java.util.List<com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot> detectedSpots,
                                       Set<String> occupiedSpotIds) {
        long freeDetected = detectedSpots.stream()
                .filter(spot -> spot.getStatus() != SpotStatus.MAINTENANCE)
                .filter(spot -> spot.getStatus() == SpotStatus.FREE)
                .count();
        if (!detectedSpots.isEmpty()) {
            return (int) freeDetected;
        }

        return parkingRepository.findById(parkingId)
                .map(parking -> Math.max(0, parking.getTotalSpaces() - occupiedSpotIds.size()))
                .orElse(0);
    }

    private String toSpotId(int row, int col) {
        return "%s%d".formatted(String.valueOf((char) ('A' + row)), col + 1);
    }
}
