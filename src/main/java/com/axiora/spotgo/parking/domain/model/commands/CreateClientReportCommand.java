package com.axiora.spotgo.parking.domain.model.commands;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReportType;

public record CreateClientReportCommand(
        String clientId,
        String parkingId,
        String reservationId,
        ReportType type,
        String date
) {
}
