package com.axiora.spotgo.parking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UpdateReservationResource(
        @Schema(description = "Updated reservation end date and time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        LocalDateTime endDate,
        @Schema(description = "Updated total amount", example = "12.5")
        Double amount,
        @Schema(description = "Updated base amount", example = "10.0")
        Double baseAmount,
        @Schema(description = "Updated rating", example = "5")
        Double rating,
        @Schema(description = "Updated reservation status", allowableValues = {"active", "completed", "cancelled"})
        String status
) {
}
