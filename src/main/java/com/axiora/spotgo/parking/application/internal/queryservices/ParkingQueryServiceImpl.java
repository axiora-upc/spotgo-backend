package com.axiora.spotgo.parking.application.internal.queryservices;

import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.queries.GetAllParkingsQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetBlueprintsByParkingIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetParkingByIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetReservationsBySpotIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetSpotsByBlueprintIdQuery;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.BlueprintRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.DetectedSpotRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingQueryServiceImpl implements ParkingQueryService {

    private final ParkingRepository parkingRepository;
    private final BlueprintRepository blueprintRepository;
    private final DetectedSpotRepository detectedSpotRepository;
    private final ReservationRepository reservationRepository;

    public ParkingQueryServiceImpl(ParkingRepository parkingRepository, BlueprintRepository blueprintRepository, DetectedSpotRepository detectedSpotRepository, ReservationRepository reservationRepository) {
        this.parkingRepository = parkingRepository;
        this.blueprintRepository = blueprintRepository;
        this.detectedSpotRepository = detectedSpotRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Optional<Parking> handle(GetParkingByIdQuery query) {
        return parkingRepository.findById(query.parkingId());
    }

    @Override
    public List<Parking> handle(GetAllParkingsQuery query) {
        return parkingRepository.findAll();
    }

    @Override
    public List<Blueprint> handle(GetBlueprintsByParkingIdQuery query) {
        return blueprintRepository.findByParkingId(query.parkingId());
    }

    @Override
    public List<DetectedSpot> handle(GetSpotsByBlueprintIdQuery query) {
        return detectedSpotRepository.findByBlueprintId(query.blueprintId());
    }

    @Override
    public List<Reservation> handle(GetReservationsBySpotIdQuery query) {
        return reservationRepository.findBySpotId(query.spotId());
    }
}
