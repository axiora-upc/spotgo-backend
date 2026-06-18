package com.axiora.spotgo.parking.interfaces.rest.resources;

import java.time.LocalDateTime;

public record CreateReservationResource(
        String vehiclePlate,
        Long spotId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
