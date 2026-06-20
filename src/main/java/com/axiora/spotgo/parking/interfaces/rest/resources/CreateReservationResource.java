package com.axiora.spotgo.parking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record CreateReservationResource(
        Long clientId,
        Long parkingId,
        String code,
        String spot,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        LocalDateTime startDate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        LocalDateTime endDate,
        Double amount,
        Double baseAmount,
        Double rating
) {}
