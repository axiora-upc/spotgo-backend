package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.commands.UpdateSpotStatusCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetSpotsByBlueprintIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetDetectedSpotsByParkingIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetAllDetectedSpotsQuery;
import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.interfaces.rest.resources.DetectedSpotResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.CreateDetectedSpotResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/detectedSpots")
@Tag(name = "DetectedSpots")
public class DetectedSpotsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public DetectedSpotsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @GetMapping
    public ResponseEntity<List<DetectedSpotResource>> getAllDetectedSpots(
            @RequestParam(required = false) Long parkingId,
            @RequestParam(required = false) Long blueprintId) {
        List<DetectedSpot> spots;
        if (parkingId != null) {
            spots = parkingQueryService.handle(new GetDetectedSpotsByParkingIdQuery(parkingId));
        } else if (blueprintId != null) {
            spots = parkingQueryService.handle(new GetSpotsByBlueprintIdQuery(blueprintId));
        } else {
            spots = parkingQueryService.handle(new GetAllDetectedSpotsQuery());
        }
        var resources = spots.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/blueprint/{blueprintId}")
    public ResponseEntity<List<DetectedSpotResource>> getSpotsByBlueprintId(@PathVariable Long blueprintId) {
        var spots = parkingQueryService.handle(new GetSpotsByBlueprintIdQuery(blueprintId));
        var resources = spots.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{spotId}/status")
    public ResponseEntity<Void> updateSpotStatus(@PathVariable Long spotId, @RequestParam String status) {
        var spotStatus = SpotStatus.fromDisplayName(status);
        var command = new UpdateSpotStatusCommand(spotId, spotStatus);
        var updatedSpotOptional = parkingCommandService.handle(command);
        if (updatedSpotOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<DetectedSpotResource> createDetectedSpot(@RequestBody CreateDetectedSpotResource resource) {
        var status = SpotStatus.fromDisplayName(resource.status());
        var command = new com.axiora.spotgo.parking.domain.model.commands.CreateDetectedSpotCommand(
                resource.localId(), resource.blueprintId(), resource.parkingId(),
                resource.row(), resource.col(),
                resource.xPct(), resource.yPct(), resource.wPct(), resource.hPct(),
                status);
        var spotOptional = parkingCommandService.handle(command);
        if (spotOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var createdSpot = spotOptional.get();
        return new ResponseEntity<>(toResource(createdSpot), HttpStatus.CREATED);
    }

    private DetectedSpotResource toResource(DetectedSpot spot) {
        return new DetectedSpotResource(
                spot.getId(),
                spot.getLocalId(),
                spot.getBlueprintId(),
                spot.getParkingId(),
                spot.getRow(),
                spot.getCol(),
                spot.getXPct(),
                spot.getYPct(),
                spot.getWPct(),
                spot.getHPct(),
                spot.getStatus().getDisplayName()
        );
    }
}
