package com.axiora.spotgo.parking.interfaces.rest.resources;

public record BlueprintResource(
        Long id,
        Long adminId,
        Long parkingId,
        String name,
        String dataUrl
) {}
