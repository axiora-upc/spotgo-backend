package com.axiora.spotgo.parking.interfaces.rest.resources;

public record CreateBlueprintResource(
        Long adminId,
        Long parkingId,
        String name,
        String dataUrl
) {}
