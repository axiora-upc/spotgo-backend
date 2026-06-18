package com.axiora.spotgo.parking.interfaces.rest.resources;

public record CreateParkingResource(
        String name,
        String location,
        Integer totalSpots
) {}
