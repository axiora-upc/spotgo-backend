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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detectedSpots")
@Tag(name = "DetectedSpots", description = "Endpoints for managing detected parking spots")
public class DetectedSpotsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public DetectedSpotsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all detected spots", description = "Returns a list of detected parking spots, optionally filtered by parking ID or blueprint ID.")
    @ApiResponse(responseCode = "200", description = "List of detected spots returned",
            content = @Content(schema = @Schema(implementation = DetectedSpotResource.class)))
    public ResponseEntity<List<DetectedSpotResource>> getAllDetectedSpots(
            @RequestParam(required = false) String parkingId,
            @RequestParam(required = false) String blueprintId) {
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
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/blueprint/{blueprintId}")
    @Operation(summary = "Get spots by blueprint ID", description = "Returns all detected spots for a specific blueprint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spots returned",
                    content = @Content(schema = @Schema(implementation = DetectedSpotResource.class))),
            @ApiResponse(responseCode = "404", description = "Blueprint not found")
    })
    public ResponseEntity<List<DetectedSpotResource>> getSpotsByBlueprintId(@PathVariable String blueprintId) {
        var spots = parkingQueryService.handle(new GetSpotsByBlueprintIdQuery(blueprintId));
        var resources = spots.stream()
                .map(this::toResource)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{spotId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update spot status", description = "Updates the status of a detected parking spot.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public ResponseEntity<Void> updateSpotStatus(@PathVariable String spotId, @RequestParam String status) {
        var spotStatus = SpotStatus.fromDisplayName(status);
        var command = new UpdateSpotStatusCommand(spotId, spotStatus);
        var updatedSpotOptional = parkingCommandService.handle(command);
        if (updatedSpotOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a detected spot", description = "Creates a new detected parking spot on a blueprint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detected spot created successfully",
                    content = @Content(schema = @Schema(implementation = DetectedSpotResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<DetectedSpotResource> createDetectedSpot(@Valid @RequestBody CreateDetectedSpotResource resource) {
        var status = SpotStatus.fromDisplayName(resource.status());
        var command = new com.axiora.spotgo.parking.domain.model.commands.CreateDetectedSpotCommand(
                resource.code(), resource.blueprintId(), resource.parkingId(),
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
                spot.getCode(),
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
