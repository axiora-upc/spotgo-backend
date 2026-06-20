package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.commands.CreateBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetBlueprintsByParkingIdQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetAllBlueprintsQuery;
import com.axiora.spotgo.parking.domain.model.commands.DeleteBlueprintCommand;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.interfaces.rest.resources.BlueprintResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.CreateBlueprintResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/blueprints")
@Tag(name = "Blueprints")
public class BlueprintsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public BlueprintsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @PostMapping
    public ResponseEntity<BlueprintResource> createBlueprint(@RequestBody CreateBlueprintResource resource) {
        var command = new CreateBlueprintCommand(resource.adminId(), resource.parkingId(), resource.name(), resource.dataUrl());
        var blueprintOptional = parkingCommandService.handle(command);
        if (blueprintOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var createdBlueprint = blueprintOptional.get();
        var blueprintResource = toResource(createdBlueprint);
        return new ResponseEntity<>(blueprintResource, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BlueprintResource>> getAllBlueprints(@RequestParam(required = false) Long parkingId) {
        List<Blueprint> blueprints;
        if (parkingId != null) {
            blueprints = parkingQueryService.handle(new GetBlueprintsByParkingIdQuery(parkingId));
        } else {
            blueprints = parkingQueryService.handle(new GetAllBlueprintsQuery());
        }
        var resources = blueprints.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/parking/{parkingId}")
    public ResponseEntity<List<BlueprintResource>> getBlueprintsByParkingId(@PathVariable Long parkingId) {
        var blueprints = parkingQueryService.handle(new GetBlueprintsByParkingIdQuery(parkingId));
        var resources = blueprints.stream()
                .map(this::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{blueprintId}")
    public ResponseEntity<Void> deleteBlueprint(@PathVariable Long blueprintId) {
        parkingCommandService.handle(new DeleteBlueprintCommand(blueprintId));
        return ResponseEntity.noContent().build();
    }

    private BlueprintResource toResource(Blueprint blueprint) {
        return new BlueprintResource(
                blueprint.getId(),
                blueprint.getAdminId(),
                blueprint.getParkingId(),
                blueprint.getName(),
                blueprint.getDataUrl()
        );
    }
}
