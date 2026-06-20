package com.axiora.spotgo.monitoring.application.internal.commandservices;

import com.axiora.spotgo.monitoring.domain.model.aggregates.Employee;
import com.axiora.spotgo.monitoring.domain.model.commands.CreateEmployeeCommand;
import com.axiora.spotgo.monitoring.domain.model.commands.DeleteEmployeeCommand;
import com.axiora.spotgo.monitoring.domain.model.commands.UpdateEmployeeCommand;

import java.util.Optional;

public interface MonitoringCommandService {
    Optional<Employee> handle(CreateEmployeeCommand command);
    Optional<Employee> handle(UpdateEmployeeCommand command);
    void handle(DeleteEmployeeCommand command);
}
