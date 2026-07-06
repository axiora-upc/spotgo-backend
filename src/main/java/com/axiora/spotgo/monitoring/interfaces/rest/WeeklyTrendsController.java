package com.axiora.spotgo.monitoring.interfaces.rest;

import com.axiora.spotgo.monitoring.domain.model.aggregates.WeeklyTrend;
import com.axiora.spotgo.monitoring.domain.model.queries.GetAllWeeklyTrendsQuery;
import com.axiora.spotgo.monitoring.domain.model.queries.GetWeeklyTrendsByParkingIdQuery;
import com.axiora.spotgo.monitoring.application.internal.queryservices.MonitoringQueryService;
import com.axiora.spotgo.monitoring.interfaces.rest.resources.WeeklyTrendResource;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weeklyTrends")
@Tag(name = "WeeklyTrends", description = "Endpoints for reading weekly occupancy trend data")
public class WeeklyTrendsController {

    private final MonitoringQueryService monitoringQueryService;

    public WeeklyTrendsController(MonitoringQueryService monitoringQueryService) {
        this.monitoringQueryService = monitoringQueryService;
    }

    @GetMapping
    @Operation(summary = "Get weekly trends", description = "Returns weekly trend points, optionally filtered by parking ID.")
    @ApiResponse(responseCode = "200", description = "List of weekly trend points returned",
            content = @Content(schema = @Schema(implementation = WeeklyTrendResource.class)))
    public ResponseEntity<List<WeeklyTrendResource>> getWeeklyTrends(@RequestParam(required = false) String parkingId) {
        List<WeeklyTrend> points;
        if (parkingId != null) {
            points = monitoringQueryService.handle(new GetWeeklyTrendsByParkingIdQuery(parkingId));
        } else {
            points = monitoringQueryService.handle(new GetAllWeeklyTrendsQuery());
        }
        var resources = points.stream().map(this::toResource).toList();
        return ResponseEntity.ok(resources);
    }

    private WeeklyTrendResource toResource(WeeklyTrend point) {
        return new WeeklyTrendResource(point.getId(), point.getParkingId(), point.getDay(), point.getValue());
    }
}
