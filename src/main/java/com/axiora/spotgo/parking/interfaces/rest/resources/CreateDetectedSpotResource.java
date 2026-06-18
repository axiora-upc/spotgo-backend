package com.axiora.spotgo.parking.interfaces.rest.resources;

public record CreateDetectedSpotResource(
        Double x,
        Double y,
        Long blueprintId
) {}
