package com.axiora.spotgo.parking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateReservationResource(
        @Schema(description = "Client identifier", example = "3")
        @NotBlank
        String clientId,

        @Schema(description = "Parking identifier", example = "1")
        @NotBlank
        String parkingId,

        @Schema(description = "Reservation code", example = "SPG-A1B2C3")
        @NotBlank
        String code,

        @Schema(description = "Reserved spot", example = "B5")
        @NotBlank
        String spot,

        @Schema(description = "Reservation start date and time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        @NotNull
        LocalDateTime startDate,

        @Schema(description = "Reservation end date and time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        @NotNull
        LocalDateTime endDate,

        @Schema(description = "Amount charged, after discounts", example = "0.05")
        Double amount,

        @Schema(description = "Base amount before discounts", example = "0.05")
        Double baseAmount,

        @Schema(description = "Client rating for this reservation", example = "5")
        Double rating
) {}
