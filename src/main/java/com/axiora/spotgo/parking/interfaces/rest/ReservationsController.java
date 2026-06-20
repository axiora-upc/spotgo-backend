package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.commands.ReserveSpotCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetReservationsBySpotIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetAllReservationsQuery;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.interfaces.rest.resources.CreateReservationResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.ReservationResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservations")
public class ReservationsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public ReservationsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @PostMapping
    public ResponseEntity<ReservationResource> reserveSpot(@RequestBody CreateReservationResource resource) {
        var command = new ReserveSpotCommand(
                resource.vehiclePlate(),
                resource.spotId(),
                resource.startTime(),
                resource.endTime()
        );
        var reservationOptional = parkingCommandService.handle(command);
        if (reservationOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        var createdReservation = reservationOptional.get();
        var reservationResource = new ReservationResource(
                createdReservation.getId(),
                resource.vehiclePlate(),
                resource.spotId(),
                resource.startTime(),
                resource.endTime(),
                "ACTIVE",
                0.0
        );
        return new ResponseEntity<>(reservationResource, HttpStatus.CREATED);
    }

    @GetMapping("/spot/{spotId}")
    public ResponseEntity<List<ReservationResource>> getReservationsBySpotId(@PathVariable Long spotId) {
        var reservations = parkingQueryService.handle(new GetReservationsBySpotIdQuery(spotId));
        var resources = reservations.stream()
                .map(reservation -> new ReservationResource(
                        reservation.getId(),
                        reservation.getVehiclePlate(),
                        reservation.getSpotId(),
                        reservation.getStartTime(),
                        reservation.getEndTime(),
                        reservation.getStatus().name(),
                        reservation.getPenalty()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResource>> getAllReservations() {
        var reservations = parkingQueryService.handle(new GetAllReservationsQuery());
        var resources = reservations.stream()
                .map(reservation -> new ReservationResource(
                        reservation.getId(),
                        reservation.getVehiclePlate(),
                        reservation.getSpotId(),
                        reservation.getStartTime(),
                        reservation.getEndTime(),
                        reservation.getStatus().name(),
                        reservation.getPenalty()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }
}
