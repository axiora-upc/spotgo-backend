package com.axiora.spotgo.parking.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateClientReportResource(
        @Schema(description = "Client identifier", example = "3")
        Long clientId,

        @Schema(description = "Parking identifier", example = "1")
        Long parkingId,

        @Schema(description = "Reservation this report refers to", example = "5")
        Long reservationId,

        @Schema(description = "Report type", example = "safety-concern",
                allowableValues = {"safety-concern", "maintenance-issue", "billing-dispute", "other"})
        String type,

        @Schema(description = "Date the report was filed", example = "2026-06-20")
        String date
) {}
