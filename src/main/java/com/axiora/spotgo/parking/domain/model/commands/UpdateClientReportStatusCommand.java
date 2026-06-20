package com.axiora.spotgo.parking.domain.model.commands;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReportStatus;

public record UpdateClientReportStatusCommand(Long reportId, ReportStatus status) {
}
