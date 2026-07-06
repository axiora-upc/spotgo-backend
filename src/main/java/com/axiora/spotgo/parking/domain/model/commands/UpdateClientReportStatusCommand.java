package com.axiora.spotgo.parking.domain.model.commands;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReportStatus;

public record UpdateClientReportStatusCommand(String reportId, ReportStatus status) {
}
