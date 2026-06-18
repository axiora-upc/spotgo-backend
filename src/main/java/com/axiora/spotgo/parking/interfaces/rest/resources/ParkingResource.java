package com.axiora.spotgo.parking.interfaces.rest.resources;

public record ParkingResource(
        Long id,
        String name,
        String location,
        Integer totalSpots
) {}
