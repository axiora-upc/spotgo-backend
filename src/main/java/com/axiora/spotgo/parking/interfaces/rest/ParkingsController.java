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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/parkings")
@Tag(name = "Parkings")
public class ParkingsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public ParkingsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @PostMapping
    public ResponseEntity<ParkingResource> createParking(@RequestBody CreateParkingResource resource) {
        var command = new CreateParkingCommand(resource.name(), resource.location(), resource.totalSpots(), resource.rating(), resource.pricePerHour());
        var parkingOptional = parkingCommandService.handle(command);
        if (parkingOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var createdParking = parkingOptional.get();
        var parkingResource = new ParkingResource(
                createdParking.getId(),
                createdParking.getName(),
                createdParking.getLocation(),
                createdParking.getTotalSpots(),
                createdParking.getRating(),
                createdParking.getPricePerHour()
        );
        return new ResponseEntity<>(parkingResource, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParkingResource>> getAllParkings() {
        var parkings = parkingQueryService.handle(new GetAllParkingsQuery());
        var resources = parkings.stream()
                .map(parking -> new ParkingResource(
                        parking.getId(),
                        parking.getName(),
                        parking.getLocation(),
                        parking.getTotalSpots(),
                        parking.getRating(),
                        parking.getPricePerHour()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{parkingId}")
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
        var parkingResource = new ParkingResource(
                updatedParking.getId(),
                updatedParking.getName(),
                updatedParking.getLocation(),
                updatedParking.getTotalSpots(),
                updatedParking.getRating(),
                updatedParking.getPricePerHour()
        );
        return ResponseEntity.ok(parkingResource);
    }
}
