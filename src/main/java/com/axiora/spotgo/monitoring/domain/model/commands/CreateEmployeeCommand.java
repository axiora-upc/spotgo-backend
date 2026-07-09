package com.axiora.spotgo.monitoring.domain.model.commands;

import com.axiora.spotgo.monitoring.domain.model.valueobjects.EmployeeRole;
import com.axiora.spotgo.monitoring.domain.model.valueobjects.EmployeeStatus;

public record CreateEmployeeCommand(
        String parkingId,
        String firstName,
        String lastName,
        EmployeeRole role,
        String schedule,
        String shiftStart,
        String shiftEnd,
        String assignedSpot,
        EmployeeStatus status
) {
}
