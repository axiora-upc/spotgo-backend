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
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/v1/blueprints")
@Tag(name = "Blueprints", description = "Endpoints for managing parking blueprints (maps)")
public class BlueprintsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public BlueprintsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a blueprint", description = "Creates a new blueprint (map) for a parking facility.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blueprint created successfully",
                    content = @Content(schema = @Schema(implementation = BlueprintResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<BlueprintResource> createBlueprint(@Valid @RequestBody CreateBlueprintResource resource) {
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
    @Operation(summary = "Get all blueprints", description = "Returns a list of blueprints, optionally filtered by parking ID.")
    @ApiResponse(responseCode = "200", description = "List of blueprints returned",
            content = @Content(schema = @Schema(implementation = BlueprintResource.class)))
    public ResponseEntity<List<BlueprintResource>> getAllBlueprints(@RequestParam(required = false) String parkingId) {
        List<Blueprint> blueprints;
        if (parkingId != null) {
            blueprints = parkingQueryService.handle(new GetBlueprintsByParkingIdQuery(parkingId));
        } else {
            blueprints = parkingQueryService.handle(new GetAllBlueprintsQuery());
        }
        var resources = blueprints.stream()
                .map(this::toResource)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/parking/{parkingId}")
    @Operation(summary = "Get blueprints by parking ID", description = "Returns all blueprints for a specific parking facility.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blueprints returned",
                    content = @Content(schema = @Schema(implementation = BlueprintResource.class))),
            @ApiResponse(responseCode = "404", description = "Parking not found")
    })
    public ResponseEntity<List<BlueprintResource>> getBlueprintsByParkingId(@PathVariable String parkingId) {
        var blueprints = parkingQueryService.handle(new GetBlueprintsByParkingIdQuery(parkingId));
        var resources = blueprints.stream()
                .map(this::toResource)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{blueprintId}")
    @Operation(summary = "Delete a blueprint", description = "Deletes a blueprint by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Blueprint deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Blueprint not found")
    })
    public ResponseEntity<Void> deleteBlueprint(@PathVariable String blueprintId) {
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
