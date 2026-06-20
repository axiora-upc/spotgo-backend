package com.axiora.spotgo.monitoring.application.internal.commandservices;

import com.axiora.spotgo.monitoring.domain.model.aggregates.Employee;
import com.axiora.spotgo.monitoring.domain.model.commands.CreateEmployeeCommand;
import com.axiora.spotgo.monitoring.domain.model.commands.DeleteEmployeeCommand;
import com.axiora.spotgo.monitoring.domain.model.commands.UpdateEmployeeCommand;
import com.axiora.spotgo.monitoring.infrastructure.persistence.jpa.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MonitoringCommandServiceImpl implements MonitoringCommandService {

    private final EmployeeRepository employeeRepository;

    public MonitoringCommandServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Optional<Employee> handle(CreateEmployeeCommand command) {
        var employee = new Employee(
                command.parkingId(), command.firstName(), command.lastName(), command.role(),
                command.schedule(), command.shiftStart(), command.shiftEnd(), command.status());
        return Optional.of(employeeRepository.save(employee));
    }

    @Override
    public Optional<Employee> handle(UpdateEmployeeCommand command) {
        var employeeOpt = employeeRepository.findById(command.employeeId());
        if (employeeOpt.isEmpty()) return Optional.empty();
        var employee = employeeOpt.get();
        employee.update(
                command.firstName(), command.lastName(), command.role(),
                command.schedule(), command.shiftStart(), command.shiftEnd(), command.status());
        return Optional.of(employeeRepository.save(employee));
    }

    @Override
    public void handle(DeleteEmployeeCommand command) {
        employeeRepository.deleteById(command.employeeId());
    }
}
