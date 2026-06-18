package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.commands.CreateBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetBlueprintsByParkingIdQuery;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.interfaces.rest.resources.BlueprintResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.CreateBlueprintResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public BlueprintsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @PostMapping
    public ResponseEntity<BlueprintResource> createBlueprint(@RequestBody CreateBlueprintResource resource) {
        var command = new CreateBlueprintCommand(resource.imageUrl(), resource.parkingId());
        var blueprintOptional = parkingCommandService.handle(command);
        if (blueprintOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        var createdBlueprint = blueprintOptional.get();
        var blueprintResource = new BlueprintResource(
                createdBlueprint.getId(),
                createdBlueprint.getImageUrl(),
                createdBlueprint.getParkingId()
        );
        return new ResponseEntity<>(blueprintResource, HttpStatus.CREATED);
    }

    @GetMapping("/parking/{parkingId}")
    public ResponseEntity<List<BlueprintResource>> getBlueprintsByParkingId(@PathVariable Long parkingId) {
        var blueprints = parkingQueryService.handle(new GetBlueprintsByParkingIdQuery(parkingId));
        var resources = blueprints.stream()
                .map(blueprint -> new BlueprintResource(
                        blueprint.getId(),
                        blueprint.getImageUrl(),
                        blueprint.getParkingId()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }
}
