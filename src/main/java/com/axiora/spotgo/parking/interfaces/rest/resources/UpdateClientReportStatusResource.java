package com.axiora.spotgo.parking.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

// frontend sends { "status": "..." } in the body, not a query param
public record UpdateClientReportStatusResource(
        @Schema(description = "New report status", example = "resolved",
                allowableValues = {"submitted", "resolved"})
        String status
) {}
