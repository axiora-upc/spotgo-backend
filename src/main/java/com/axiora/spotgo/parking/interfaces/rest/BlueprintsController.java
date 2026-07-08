package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.iam.infrastructure.security.SpotgoUserPrincipal;
import com.axiora.spotgo.parking.domain.model.aggregates.Blueprint;
import com.axiora.spotgo.parking.domain.model.commands.CreateBlueprintCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetAllBlueprintsQuery;
import com.axiora.spotgo.parking.domain.model.queries.GetBlueprintsByParkingIdQuery;
import com.axiora.spotgo.parking.domain.model.commands.DeleteBlueprintCommand;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.BlueprintRepository;
import com.axiora.spotgo.parking.interfaces.rest.resources.BlueprintResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.CreateBlueprintResource;
import com.axiora.spotgo.shared.application.security.AuthorizationService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blueprints")
@Tag(name = "Blueprints", description = "Endpoints for managing parking blueprints (maps)")
public class BlueprintsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;
    private final BlueprintRepository blueprintRepository;
    private final AuthorizationService authorizationService;

    public BlueprintsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService,
                                BlueprintRepository blueprintRepository, AuthorizationService authorizationService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
        this.blueprintRepository = blueprintRepository;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a blueprint", description = "Creates a new blueprint (map) for a parking facility.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blueprint created successfully",
                    content = @Content(schema = @Schema(implementation = BlueprintResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<BlueprintResource> createBlueprint(@AuthenticationPrincipal SpotgoUserPrincipal principal,
                                                             @Valid @RequestBody CreateBlueprintResource resource) {
        authorizationService.requireParkingOwnership(principal, resource.parkingId());
        var command = new CreateBlueprintCommand(principal.getUserId(), resource.parkingId(), resource.name(), resource.dataUrl());
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
    public ResponseEntity<List<BlueprintResource>> getAllBlueprints(@AuthenticationPrincipal SpotgoUserPrincipal principal,
                                                                    @RequestParam(required = false) String parkingId) {
        List<Blueprint> blueprints;
        if (authorizationService.isAdmin(principal)) {
            var scopedParkingId = authorizationService.requireAdminParkingId(principal);
            if (parkingId != null && !parkingId.equals(scopedParkingId)) {
                throw new org.springframework.security.access.AccessDeniedException("Requested parking is outside authenticated scope");
            }
            blueprints = parkingQueryService.handle(new GetBlueprintsByParkingIdQuery(scopedParkingId));
        } else if (parkingId != null) {
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
    public ResponseEntity<List<BlueprintResource>> getBlueprintsByParkingId(@AuthenticationPrincipal SpotgoUserPrincipal principal,
                                                                            @PathVariable String parkingId) {
        if (authorizationService.isAdmin(principal)) {
            authorizationService.requireParkingOwnership(principal, parkingId);
        }
        var blueprints = parkingQueryService.handle(new GetBlueprintsByParkingIdQuery(parkingId));
        var resources = blueprints.stream()
                .map(this::toResource)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{blueprintId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a blueprint", description = "Deletes a blueprint by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Blueprint deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Blueprint not found")
    })
    public ResponseEntity<Void> deleteBlueprint(@AuthenticationPrincipal SpotgoUserPrincipal principal,
                                                @PathVariable String blueprintId) {
        var blueprint = blueprintRepository.findById(blueprintId)
                .orElseThrow(() -> new IllegalArgumentException("Blueprint not found"));
        authorizationService.requireBlueprintOwnership(principal, blueprint);
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
