package com.axiora.spotgo.parking.interfaces.rest.resources;

public record DetectedSpotResource(
        Long id,
        Double x,
        Double y,
        String status,
        Long blueprintId
) {}
