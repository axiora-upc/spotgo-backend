package com.axiora.spotgo.parking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateDetectedSpotResource(
        @Schema(description = "Local identifier within the blueprint", example = "5")
        Integer localId,

        @Schema(description = "Blueprint identifier this spot belongs to", example = "1")
        Long blueprintId,

        @Schema(description = "Parking identifier", example = "1")
        Long parkingId,

        @Schema(description = "Row position on the blueprint grid", example = "0")
        Integer row,

        @Schema(description = "Column position on the blueprint grid", example = "0")
        Integer col,

        @Schema(description = "X position as a percentage of the blueprint width", example = "5.84")
        @JsonProperty("x_pct") Double xPct,

        @Schema(description = "Y position as a percentage of the blueprint height", example = "8.05")
        @JsonProperty("y_pct") Double yPct,

        @Schema(description = "Width as a percentage of the blueprint width", example = "7.73")
        @JsonProperty("w_pct") Double wPct,

        @Schema(description = "Height as a percentage of the blueprint height", example = "16.33")
        @JsonProperty("h_pct") Double hPct,

        @Schema(description = "Spot status", example = "available",
                allowableValues = {"available", "occupied", "reserved", "maintenance"})
        String status
) {}
