package com.axiora.spotgo.parking.application.internal.commandservices;

import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.aggregates.ClientReport;
import com.axiora.spotgo.parking.domain.model.commands.CreateBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.commands.CreateParkingCommand;
import com.axiora.spotgo.parking.domain.model.commands.ReserveSpotCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateSpotStatusCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateParkingRatingCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateParkingCommand;
import com.axiora.spotgo.parking.domain.model.commands.CreateDetectedSpotCommand;
import com.axiora.spotgo.parking.domain.model.commands.DeleteBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.commands.CreateClientReportCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateClientReportStatusCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateReservationCommand;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.BlueprintRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.DetectedSpotRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ParkingRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ReservationRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ClientReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class ParkingCommandServiceImpl implements ParkingCommandService {

    private final ParkingRepository parkingRepository;
    private final BlueprintRepository blueprintRepository;
    private final DetectedSpotRepository detectedSpotRepository;
    private final ReservationRepository reservationRepository;
    private final ClientReportRepository clientReportRepository;

    public ParkingCommandServiceImpl(ParkingRepository parkingRepository, BlueprintRepository blueprintRepository, DetectedSpotRepository detectedSpotRepository, ReservationRepository reservationRepository, ClientReportRepository clientReportRepository) {
        this.parkingRepository = parkingRepository;
        this.blueprintRepository = blueprintRepository;
        this.detectedSpotRepository = detectedSpotRepository;
        this.reservationRepository = reservationRepository;
        this.clientReportRepository = clientReportRepository;
    }

    @Override
    public Optional<Parking> handle(CreateParkingCommand command) {
        var parking = new Parking(
                command.adminId(), command.name(), command.address(), command.city(),
                command.totalSpaces(), command.availableSpaces(), command.totalFloors(),
                command.averageOccupancy(), command.occupancyTrendPercent(), command.peakHour(),
                command.totalRevenue(), command.systemStatus(), command.rating(), command.pricePerHour(),
                command.revenueTrendPercent(), command.totalCapacity(), command.efficiencyIndex());
        return Optional.of(parkingRepository.save(parking));
    }

    @Override
    public Optional<Blueprint> handle(CreateBlueprintCommand command) {
        if (!parkingRepository.existsById(command.parkingId())) {
            throw new IllegalArgumentException("Parking does not exist");
        }
        var blueprint = new Blueprint(command.adminId(), command.parkingId(), command.name(), command.dataUrl());
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
        var reservation = new Reservation(
                command.clientId(), command.parkingId(), command.code(), command.spot(),
                command.startDate(), command.endDate(),
                command.amount(), command.baseAmount(), command.rating());
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
        var spot = new DetectedSpot(
                command.localId(), command.blueprintId(), command.parkingId(),
                command.row(), command.col(),
                command.xPct(), command.yPct(), command.wPct(), command.hPct(),
                command.status());
        return Optional.of(detectedSpotRepository.save(spot));
    }

    @Override
    public void handle(DeleteBlueprintCommand command) {
        blueprintRepository.deleteById(command.blueprintId());
    }

    @Override
    public Optional<ClientReport> handle(CreateClientReportCommand command) {
        var nextCode = clientReportRepository.findTopByOrderByCodeDesc()
                .map(ClientReport::getCode)
                .map(this::incrementReportCode)
                .orElse("RPT-00001");
        var report = new ClientReport(
                command.clientId(), command.parkingId(), command.reservationId(),
                nextCode, command.type(), parseDateTime(command.date()));
        return Optional.of(clientReportRepository.save(report));
    }

    @Override
    public Optional<ClientReport> handle(UpdateClientReportStatusCommand command) {
        var reportOpt = clientReportRepository.findById(command.reportId());
        if (reportOpt.isEmpty()) return Optional.empty();
        var report = reportOpt.get();
        report.updateStatus(command.status());
        return Optional.of(clientReportRepository.save(report));
    }

    @Override
    public Optional<Parking> handle(UpdateParkingCommand command) {
        var parkingOpt = parkingRepository.findById(command.parkingId());
        if (parkingOpt.isEmpty()) {
            return Optional.empty();
        }
        var parking = parkingOpt.get();
        parking.updateStats(command.totalSpaces(), command.availableSpaces(), command.totalFloors(), command.rating());
        return Optional.of(parkingRepository.save(parking));
    }

    @Override
    public Optional<Reservation> handle(UpdateReservationCommand command) {
        var reservationOpt = reservationRepository.findById(command.reservationId());
        if (reservationOpt.isEmpty()) {
            return Optional.empty();
        }
        var reservation = reservationOpt.get();
        reservation.updateDetails(command.endDate(), command.amount(), command.baseAmount(), command.rating(), command.status());
        return Optional.of(reservationRepository.save(reservation));
    }

    private String incrementReportCode(String currentCode) {
        var numericPortion = currentCode.replace("RPT-", "");
        var next = Integer.parseInt(numericPortion) + 1;
        return "RPT-%05d".formatted(next);
    }

    private Instant parseDateTime(String value) {
        return Instant.parse(value);
    }
}
