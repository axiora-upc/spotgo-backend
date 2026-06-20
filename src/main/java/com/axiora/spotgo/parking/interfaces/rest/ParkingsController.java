package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.parking.domain.model.aggregates.Parking;
import com.axiora.spotgo.parking.domain.model.commands.CreateParkingCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetAllParkingsQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetParkingByIdQuery;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.interfaces.rest.resources.CreateParkingResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.ParkingResource;
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
@RequestMapping("/api/v1/parkings")
@Tag(name = "Parkings", description = "Endpoints for managing parking facilities")
public class ParkingsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public ParkingsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a parking", description = "Creates a new parking facility.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parking created successfully",
                    content = @Content(schema = @Schema(implementation = ParkingResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ParkingResource> createParking(@RequestBody CreateParkingResource resource) {
        var command = new CreateParkingCommand(
                resource.adminId(), resource.name(), resource.address(), resource.city(),
                resource.totalSpaces(), resource.availableSpaces(), resource.totalFloors(),
                resource.averageOccupancy(), resource.occupancyTrendPercent(), resource.peakHour(),
                resource.totalRevenue(), resource.systemStatus(), resource.rating(), resource.pricePerHour(),
                resource.revenueTrendPercent(), resource.totalCapacity(), resource.efficiencyIndex());
        var parkingOptional = parkingCommandService.handle(command);
        if (parkingOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var createdParking = parkingOptional.get();
        var parkingResource = toResource(createdParking);
        return new ResponseEntity<>(parkingResource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all parkings", description = "Returns a list of all parking facilities.")
    @ApiResponse(responseCode = "200", description = "List of parkings returned",
            content = @Content(schema = @Schema(implementation = ParkingResource.class)))
    public ResponseEntity<List<ParkingResource>> getAllParkings() {
        var parkings = parkingQueryService.handle(new GetAllParkingsQuery());
        var resources = parkings.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{parkingId}")
    @Operation(summary = "Update parking rating", description = "Updates the rating of a parking facility.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully",
                    content = @Content(schema = @Schema(implementation = ParkingResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Parking not found")
    })
    public ResponseEntity<ParkingResource> updateParkingRating(@PathVariable Long parkingId, @RequestBody java.util.Map<String, Object> body) {
        if (!body.containsKey("rating")) {
            return ResponseEntity.badRequest().build();
        }
        var ratingVal = body.get("rating");
        Double rating = null;
        if (ratingVal instanceof Number) {
            rating = ((Number) ratingVal).doubleValue();
        }
        if (rating == null) {
            return ResponseEntity.badRequest().build();
        }
        var command = new com.axiora.spotgo.parking.domain.model.commands.UpdateParkingRatingCommand(parkingId, rating);
        var updatedParkingOpt = parkingCommandService.handle(command);
        if (updatedParkingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var updatedParking = updatedParkingOpt.get();
        return ResponseEntity.ok(toResource(updatedParking));
    }

    private ParkingResource toResource(Parking parking) {
        return new ParkingResource(
                parking.getId(),
                parking.getAdminId(),
                parking.getName(),
                parking.getAddress(),
                parking.getCity(),
                parking.getTotalSpaces(),
                parking.getAvailableSpaces(),
                parking.getTotalFloors(),
                parking.getAverageOccupancy(),
                parking.getOccupancyTrendPercent(),
                parking.getPeakHour(),
                parking.getTotalRevenue(),
                parking.getSystemStatus(),
                parking.getRating(),
                parking.getPricePerHour(),
                parking.getRevenueTrendPercent(),
                parking.getTotalCapacity(),
                parking.getEfficiencyIndex()
        );
    }
}
