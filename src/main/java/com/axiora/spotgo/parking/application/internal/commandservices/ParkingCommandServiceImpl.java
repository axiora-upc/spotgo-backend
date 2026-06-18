package com.axiora.spotgo.parking.application.internal.commandservices;

import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.commands.CreateBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.commands.CreateParkingCommand;
import com.axiora.spotgo.parking.domain.model.commands.ReserveSpotCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateSpotStatusCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateParkingRatingCommand;
import com.axiora.spotgo.parking.domain.model.commands.CreateDetectedSpotCommand;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.BlueprintRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.DetectedSpotRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParkingCommandServiceImpl implements ParkingCommandService {

    private final ParkingRepository parkingRepository;
    private final BlueprintRepository blueprintRepository;
    private final DetectedSpotRepository detectedSpotRepository;
    private final ReservationRepository reservationRepository;

    public ParkingCommandServiceImpl(ParkingRepository parkingRepository, BlueprintRepository blueprintRepository, DetectedSpotRepository detectedSpotRepository, ReservationRepository reservationRepository) {
        this.parkingRepository = parkingRepository;
        this.blueprintRepository = blueprintRepository;
        this.detectedSpotRepository = detectedSpotRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Optional<Parking> handle(CreateParkingCommand command) {
        var parking = new Parking(command.name(), command.location(), command.totalSpots(), command.rating(), command.pricePerHour());
        return Optional.of(parkingRepository.save(parking));
    }

    @Override
    public Optional<Blueprint> handle(CreateBlueprintCommand command) {
        if (!parkingRepository.existsById(command.parkingId())) {
            throw new IllegalArgumentException("Parking does not exist");
        }
        var blueprint = new Blueprint(command.imageUrl(), command.parkingId());
        return Optional.of(blueprintRepository.save(blueprint));
    }

    @Override
    public Optional<DetectedSpot> handle(UpdateSpotStatusCommand command) {
        var spot = detectedSpotRepository.findById(command.spotId());
        if (spot.isEmpty()) return Optional.empty();
        var detectedSpot = spot.get();
        detectedSpot.updateStatus(command.status());
        return Optional.of(detectedSpotRepository.save(detectedSpot));
    }

    @Override
    public Optional<Reservation> handle(ReserveSpotCommand command) {
        if (!detectedSpotRepository.existsById(command.spotId())) {
            throw new IllegalArgumentException("Spot does not exist");
        }
        var reservation = new Reservation(command.vehiclePlate(), command.spotId(), command.startTime(), command.endTime());
        return Optional.of(reservationRepository.save(reservation));
    }

    @Override
    public Optional<Parking> handle(UpdateParkingRatingCommand command) {
        var parkingOpt = parkingRepository.findById(command.parkingId());
        if (parkingOpt.isEmpty()) {
            return Optional.empty();
        }
        var parking = parkingOpt.get();
        parking.updateRating(command.rating());
        return Optional.of(parkingRepository.save(parking));
    }

    @Override
    public Optional<DetectedSpot> handle(CreateDetectedSpotCommand command) {
        if (!blueprintRepository.existsById(command.blueprintId())) {
            throw new IllegalArgumentException("Blueprint does not exist");
        }
        var spot = new DetectedSpot(command.coordinates(), command.blueprintId());
        return Optional.of(detectedSpotRepository.save(spot));
    }
}
