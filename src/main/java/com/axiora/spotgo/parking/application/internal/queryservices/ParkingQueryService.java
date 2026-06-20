package com.axiora.spotgo.parking.application.internal.queryservices;

import com.axiora.spotgo.parking.domain.model.queries.GetAllParkingsQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetBlueprintsByParkingIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetParkingByIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetSpotsByBlueprintIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetAllReservationsQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetReservationsByParkingIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetDetectedSpotsByParkingIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetAllDetectedSpotsQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetAllBlueprintsQuery;
import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;

import java.util.List;
import java.util.Optional;

public interface ParkingQueryService {
    Optional<Parking> handle(GetParkingByIdQuery query);
    List<Parking> handle(GetAllParkingsQuery query);
    List<Blueprint> handle(GetBlueprintsByParkingIdQuery query);
    List<DetectedSpot> handle(GetSpotsByBlueprintIdQuery query);
    List<Reservation> handle(GetAllReservationsQuery query);
    List<Reservation> handle(GetReservationsByParkingIdQuery query);
    List<DetectedSpot> handle(GetDetectedSpotsByParkingIdQuery query);
    List<DetectedSpot> handle(GetAllDetectedSpotsQuery query);
    List<Blueprint> handle(GetAllBlueprintsQuery query);
}
