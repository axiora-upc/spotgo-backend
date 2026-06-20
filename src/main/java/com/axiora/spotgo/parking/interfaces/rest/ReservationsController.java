package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.commands.ReserveSpotCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetAllReservationsQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetReservationsByParkingIdQuery;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.interfaces.rest.resources.CreateReservationResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.ReservationResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservations", description = "Endpoints for managing parking spot reservations")
public class ReservationsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public ReservationsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @PostMapping
    @Operation(summary = "Reserve a spot", description = "Creates a new parking spot reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                    content = @Content(schema = @Schema(implementation = ReservationResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ReservationResource> reserveSpot(@RequestBody CreateReservationResource resource) {
        var command = new ReserveSpotCommand(
                resource.clientId(),
                resource.parkingId(),
                resource.code(),
                resource.spot(),
                resource.startDate(),
                resource.endDate(),
                resource.amount(),
                resource.baseAmount(),
                resource.rating()
        );
        var reservationOptional = parkingCommandService.handle(command);
        if (reservationOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var createdReservation = reservationOptional.get();
        return new ResponseEntity<>(toResource(createdReservation), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all reservations", description = "Returns a list of reservations, optionally filtered by parking ID.")
    @ApiResponse(responseCode = "200", description = "List of reservations returned",
            content = @Content(schema = @Schema(implementation = ReservationResource.class)))
    public ResponseEntity<List<ReservationResource>> getAllReservations(@RequestParam(required = false) Long parkingId) {
        List<Reservation> reservations;
        if (parkingId != null) {
            reservations = parkingQueryService.handle(new GetReservationsByParkingIdQuery(parkingId));
        } else {
            reservations = parkingQueryService.handle(new GetAllReservationsQuery());
        }
        var resources = reservations.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    private ReservationResource toResource(Reservation reservation) {
        return new ReservationResource(
                reservation.getId(),
                reservation.getClientId(),
                reservation.getParkingId(),
                reservation.getCode(),
                reservation.getSpot(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus().name().toLowerCase(),
                reservation.getAmount(),
                reservation.getBaseAmount(),
                reservation.getRating()
        );
    }
}
