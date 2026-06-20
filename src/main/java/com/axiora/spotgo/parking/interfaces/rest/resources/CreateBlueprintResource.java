package com.axiora.spotgo.parking.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateBlueprintResource(
        @Schema(description = "Administrator identifier", example = "1")
        Long adminId,

        @Schema(description = "Parking identifier", example = "1")
        Long parkingId,

        @Schema(description = "Blueprint name", example = "Croquis Nivel 1")
        String name,

        @Schema(description = "URL or data URI of the blueprint image")
        String dataUrl
) {}
