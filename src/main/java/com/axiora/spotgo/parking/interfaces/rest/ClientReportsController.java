package com.axiora.spotgo.parking.interfaces.rest;

import com.axiora.spotgo.parking.domain.model.aggregates.ClientReport;
import com.axiora.spotgo.parking.domain.model.commands.CreateClientReportCommand;
import com.axiora.spotgo.parking.domain.model.commands.UpdateClientReportStatusCommand;
import com.axiora.spotgo.parking.domain.model.queries.GetAllClientReportsQuery;
import com.axiora.spotgo.parking.domain.model.valueobjects.ReportStatus;
import com.axiora.spotgo.parking.domain.model.valueobjects.ReportType;
import com.axiora.spotgo.parking.application.internal.commandservices.ParkingCommandService;
import com.axiora.spotgo.parking.application.internal.queryservices.ParkingQueryService;
import com.axiora.spotgo.parking.interfaces.rest.resources.ClientReportResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.CreateClientReportResource;
import com.axiora.spotgo.parking.interfaces.rest.resources.UpdateClientReportStatusResource;
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
@RequestMapping("/api/v1/clientReports")
@Tag(name = "ClientReports", description = "Endpoints for managing client-submitted parking reports")
public class ClientReportsController {

    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public ClientReportsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Create a client report", description = "Submits a new report from a client about a past reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Report created successfully",
                    content = @Content(schema = @Schema(implementation = ClientReportResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ClientReportResource> createClientReport(@Valid @RequestBody CreateClientReportResource resource) {
        var command = new CreateClientReportCommand(
                resource.clientId(),
                resource.parkingId(),
                resource.reservationId(),
                ReportType.fromDisplayName(resource.type()),
                resource.date()
        );
        var reportOptional = parkingCommandService.handle(command);
        if (reportOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(toResource(reportOptional.get()), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all client reports", description = "Returns the full list of client reports.")
    @ApiResponse(responseCode = "200", description = "List of client reports returned",
            content = @Content(schema = @Schema(implementation = ClientReportResource.class)))
    public ResponseEntity<List<ClientReportResource>> getAllClientReports() {
        var reports = parkingQueryService.handle(new GetAllClientReportsQuery());
        var resources = reports.stream()
                .map(this::toResource)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{reportId}")
    @Operation(summary = "Update report status", description = "Updates the status of a client report (e.g. mark as resolved).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(schema = @Schema(implementation = ClientReportResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status value or report not found")
    })
    public ResponseEntity<ClientReportResource> updateClientReportStatus(
            @PathVariable String reportId,
            @Valid @RequestBody UpdateClientReportStatusResource resource) {
        var command = new UpdateClientReportStatusCommand(reportId, ReportStatus.fromDisplayName(resource.status()));
        var reportOptional = parkingCommandService.handle(command);
        if (reportOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(toResource(reportOptional.get()));
    }

    private ClientReportResource toResource(ClientReport report) {
        return new ClientReportResource(
                report.getId(),
                report.getCode(),
                report.getClientId(),
                report.getParkingId(),
                report.getReservationId(),
                report.getType().getDisplayName(),
                report.getDate().toString(),
                report.getStatus().getDisplayName()
        );
    }
}
