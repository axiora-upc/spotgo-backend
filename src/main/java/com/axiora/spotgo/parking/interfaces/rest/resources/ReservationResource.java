package com.axiora.spotgo.parking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReservationResource(
        @Schema(description = "Unique identifier of the reservation", example = "1")
        Long id,

        @Schema(description = "Client identifier", example = "3")
        Long clientId,

        @Schema(description = "Parking identifier", example = "1")
        Long parkingId,

        @Schema(description = "Reservation code", example = "SPG-A1B2C3")
        String code,

        @Schema(description = "Reserved spot", example = "B5")
        String spot,

        @Schema(description = "Reservation start date and time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        LocalDateTime startDate,

        @Schema(description = "Reservation end date and time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        LocalDateTime endDate,

        @Schema(description = "Reservation status", example = "active",
                allowableValues = {"active", "completed", "cancelled"})
        String status,

        @Schema(description = "Amount charged, after discounts", example = "0.05")
        Double amount,

        @Schema(description = "Base amount before discounts", example = "0.05")
        Double baseAmount,

        @Schema(description = "Client rating for this reservation", example = "5")
        Double rating
) {}
