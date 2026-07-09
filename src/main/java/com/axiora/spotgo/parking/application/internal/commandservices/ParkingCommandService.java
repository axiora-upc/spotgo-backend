package com.axiora.spotgo.parking.application.internal.commandservices;

import com.axiora.spotgo.parking.domain.model.commands.CreateBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.commands.CreateParkingCommand;
import com.axiora.spotgo.parking.domain.model.commands.ReserveSpotCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateSpotStatusCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateParkingRatingCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateParkingCommand;
import com.axiora.spotgo.parking.domain.model.commands.CreateDetectedSpotCommand;
import com.axiora.spotgo.parking.domain.model.commands.DeleteBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.commands.CreateClientReportCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateClientReportStatusCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateReservationCommand;
import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.aggregates.ClientReport;

import java.util.Optional;

public interface ParkingCommandService {
    Optional<Parking> handle(CreateParkingCommand command);
    Optional<Blueprint> handle(CreateBlueprintCommand command);
    Optional<Blueprint> handle(UpdateBlueprintCommand command);
    Optional<DetectedSpot> handle(UpdateSpotStatusCommand command);
    Optional<Reservation> handle(ReserveSpotCommand command);
    Optional<Parking> handle(UpdateParkingRatingCommand command);
    Optional<DetectedSpot> handle(CreateDetectedSpotCommand command);
    void handle(DeleteBlueprintCommand command);
    Optional<ClientReport> handle(CreateClientReportCommand command);
    Optional<ClientReport> handle(UpdateClientReportStatusCommand command);
    Optional<Parking> handle(UpdateParkingCommand command);
    Optional<Reservation> handle(UpdateReservationCommand command);
}
