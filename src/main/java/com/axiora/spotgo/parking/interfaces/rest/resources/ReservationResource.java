package com.axiora.spotgo.parking.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ReservationResource(
        Long id,
        String vehiclePlate,
        Long spotId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        Double penalty
) {}
