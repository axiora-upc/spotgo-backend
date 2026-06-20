package com.axiora.spotgo.parking.domain.model.commands;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReportType;

public record CreateClientReportCommand(
        Long clientId,
        Long parkingId,
        Long reservationId,
        ReportType type,
        String date
) {
}
