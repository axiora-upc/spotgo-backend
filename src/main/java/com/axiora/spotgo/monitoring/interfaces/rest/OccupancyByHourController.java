package com.axiora.spotgo.monitoring.interfaces.rest;

import com.axiora.spotgo.monitoring.domain.model.aggregates.OccupancyByHour;
import com.axiora.spotgo.monitoring.domain.model.queries.GetAllOccupancyByHourQuery;
import com.axiora.spotgo.monitoring.domain.model.queries.GetOccupancyByHourByParkingIdQuery;
import com.axiora.spotgo.monitoring.application.internal.queryservices.MonitoringQueryService;
import com.axiora.spotgo.monitoring.interfaces.rest.resources.OccupancyByHourResource;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/occupancyByHour")
@Tag(name = "OccupancyByHour", description = "Endpoints for reading hourly occupancy data")
public class OccupancyByHourController {

    private final MonitoringQueryService monitoringQueryService;

    public OccupancyByHourController(MonitoringQueryService monitoringQueryService) {
        this.monitoringQueryService = monitoringQueryService;
    }

    @GetMapping
    @Operation(summary = "Get occupancy by hour", description = "Returns hourly occupancy points, optionally filtered by parking ID.")
    @ApiResponse(responseCode = "200", description = "List of occupancy points returned",
            content = @Content(schema = @Schema(implementation = OccupancyByHourResource.class)))
    public ResponseEntity<List<OccupancyByHourResource>> getOccupancyByHour(@RequestParam(required = false) String parkingId) {
        List<OccupancyByHour> points;
        if (parkingId != null) {
            points = monitoringQueryService.handle(new GetOccupancyByHourByParkingIdQuery(parkingId));
        } else {
            points = monitoringQueryService.handle(new GetAllOccupancyByHourQuery());
        }
        var resources = points.stream().map(this::toResource).toList();
        return ResponseEntity.ok(resources);
    }

    private OccupancyByHourResource toResource(OccupancyByHour point) {
        return new OccupancyByHourResource(point.getId(), point.getParkingId(), point.getHour(), point.getIntensity());
    }
}
