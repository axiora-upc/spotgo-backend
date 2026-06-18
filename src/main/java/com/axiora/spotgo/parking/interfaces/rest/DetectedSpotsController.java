package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.commands.UpdateSpotStatusCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetSpotsByBlueprintIdQuery;
import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.interfaces.rest.resources.DetectedSpotResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/detected-spots")
public class DetectedSpotsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public DetectedSpotsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @GetMapping("/blueprint/{blueprintId}")
    public ResponseEntity<List<DetectedSpotResource>> getSpotsByBlueprintId(@PathVariable Long blueprintId) {
        var spots = parkingQueryService.handle(new GetSpotsByBlueprintIdQuery(blueprintId));
        var resources = spots.stream()
                .map(spot -> new DetectedSpotResource(
                        spot.getId(),
                        spot.getCoordinates().getX(),
                        spot.getCoordinates().getY(),
                        spot.getStatus().name(),
                        spot.getBlueprintId()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{spotId}/status")
    public ResponseEntity<DetectedSpotResource> updateSpotStatus(@PathVariable Long spotId, @RequestParam String status) {
        try {
            var spotStatus = SpotStatus.valueOf(status.toUpperCase());
            var command = new UpdateSpotStatusCommand(spotId, spotStatus);
            var updatedSpotOptional = parkingCommandService.handle(command);
            if (updatedSpotOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            // Ideally we query the spot to return it, but keeping it simple:
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
