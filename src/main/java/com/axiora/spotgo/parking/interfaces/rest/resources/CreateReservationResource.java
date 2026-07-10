package com.axiora.spotgo.parking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateReservationResource(
        @Schema(description = "Parking identifier", example = "1")
        @NotBlank
        String parkingId,

        @Schema(description = "Reserved spot", example = "B5")
        @NotBlank
        String spot,

        @Schema(description = "Reservation start date and time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "America/Lima")
        @NotNull
        LocalDateTime startDate,

        @Schema(description = "Reservation end date and time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "America/Lima")
        @NotNull
        LocalDateTime endDate
) {}
