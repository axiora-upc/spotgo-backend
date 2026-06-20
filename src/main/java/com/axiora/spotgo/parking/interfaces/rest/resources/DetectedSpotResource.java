package com.axiora.spotgo.parking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DetectedSpotResource(
        Long id,
        Integer localId,
        Long blueprintId,
        Long parkingId,
        Integer row,
        Integer col,
        @JsonProperty("x_pct") Double xPct,
        @JsonProperty("y_pct") Double yPct,
        @JsonProperty("w_pct") Double wPct,
        @JsonProperty("h_pct") Double hPct,
        String status
) {}
