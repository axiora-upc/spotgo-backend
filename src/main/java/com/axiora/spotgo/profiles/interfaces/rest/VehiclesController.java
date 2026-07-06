package com.axiora.spotgo.profiles.interfaces.rest;

import com.axiora.spotgo.profiles.application.ProfilesCommandService;
import com.axiora.spotgo.profiles.application.ProfilesQueryService;
import com.axiora.spotgo.profiles.domain.model.aggregates.Vehicle;
import com.axiora.spotgo.profiles.domain.model.commands.CreateVehicleCommand;
import com.axiora.spotgo.profiles.domain.model.commands.DeleteVehicleCommand;
import com.axiora.spotgo.profiles.domain.model.commands.UpdateVehicleCommand;
import com.axiora.spotgo.profiles.domain.model.queries.GetAllVehiclesQuery;
import com.axiora.spotgo.profiles.domain.model.queries.GetVehiclesByClientIdQuery;
import com.axiora.spotgo.profiles.interfaces.rest.resources.CreateVehicleResource;
import com.axiora.spotgo.profiles.interfaces.rest.resources.UpdateVehicleResource;
import com.axiora.spotgo.profiles.interfaces.rest.resources.VehicleResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicles", description = "Endpoints for managing client vehicles")
public class VehiclesController {

    private final ProfilesCommandService profilesCommandService;
    private final ProfilesQueryService profilesQueryService;

    public VehiclesController(ProfilesCommandService profilesCommandService, ProfilesQueryService profilesQueryService) {
        this.profilesCommandService = profilesCommandService;
        this.profilesQueryService = profilesQueryService;
    }

    @GetMapping
    @Operation(summary = "Get vehicles", description = "Returns all vehicles, optionally filtered by client identifier.")
    @ApiResponse(responseCode = "200", description = "Vehicles returned successfully",
            content = @Content(schema = @Schema(implementation = VehicleResource.class)))
    public ResponseEntity<List<VehicleResource>> getVehicles(@RequestParam(required = false) String clientId) {
        var vehicles = clientId == null
                ? profilesQueryService.handle(new GetAllVehiclesQuery())
                : profilesQueryService.handle(new GetVehiclesByClientIdQuery(clientId));
        return ResponseEntity.ok(vehicles.stream().map(this::toResource).toList());
    }

    @PostMapping
    @Operation(summary = "Create vehicle", description = "Creates a new vehicle for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<VehicleResource> createVehicle(@Valid @RequestBody CreateVehicleResource resource) {
        var vehicle = profilesCommandService.handle(new CreateVehicleCommand(
                resource.clientId(), resource.licensePlate(), resource.vehicleType(), resource.brand(), resource.model()));
        return vehicle.map(value -> ResponseEntity.status(HttpStatus.CREATED).body(toResource(value)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{vehicleId}")
    @Operation(summary = "Update vehicle", description = "Updates mutable vehicle fields.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    public ResponseEntity<VehicleResource> updateVehicle(
            @PathVariable @Parameter(description = "Vehicle identifier") String vehicleId,
            @Valid @RequestBody UpdateVehicleResource resource) {
        var vehicle = profilesCommandService.handle(new UpdateVehicleCommand(
                vehicleId, resource.licensePlate(), resource.vehicleType(), resource.brand(), resource.model()));
        return vehicle.map(value -> ResponseEntity.ok(toResource(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{vehicleId}")
    @Operation(summary = "Delete vehicle", description = "Deletes a vehicle by identifier.")
    @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String vehicleId) {
        profilesCommandService.handle(new DeleteVehicleCommand(vehicleId));
        return ResponseEntity.noContent().build();
    }

    private VehicleResource toResource(Vehicle vehicle) {
        return new VehicleResource(vehicle.getId(), vehicle.getClientId(), vehicle.getLicensePlate(), vehicle.getVehicleType(), vehicle.getBrand(), vehicle.getModel());
    }
}
