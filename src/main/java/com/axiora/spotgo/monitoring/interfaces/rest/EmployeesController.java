package com.axiora.spotgo.monitoring.interfaces.rest;

import com.axiora.spotgo.monitoring.domain.model.aggregates.Employee;
import com.axiora.spotgo.monitoring.domain.model.commands.CreateEmployeeCommand;
import com.axiora.spotgo.monitoring.domain.model.commands.DeleteEmployeeCommand;
import com.axiora.spotgo.monitoring.domain.model.commands.UpdateEmployeeCommand;
import com.axiora.spotgo.monitoring.domain.model.queries.GetAllEmployeesQuery;
import com.axiora.spotgo.monitoring.domain.model.valueobjects.EmployeeRole;
import com.axiora.spotgo.monitoring.domain.model.valueobjects.EmployeeStatus;
import com.axiora.spotgo.monitoring.application.internal.commandservices.MonitoringCommandService;
import com.axiora.spotgo.monitoring.application.internal.queryservices.MonitoringQueryService;
import com.axiora.spotgo.monitoring.interfaces.rest.resources.CreateEmployeeResource;
import com.axiora.spotgo.monitoring.interfaces.rest.resources.EmployeeResource;
import com.axiora.spotgo.monitoring.interfaces.rest.resources.UpdateEmployeeResource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Employees", description = "Endpoints for managing parking employees")
public class EmployeesController {

    private final MonitoringCommandService monitoringCommandService;
    private final MonitoringQueryService monitoringQueryService;

    public EmployeesController(MonitoringCommandService monitoringCommandService, MonitoringQueryService monitoringQueryService) {
        this.monitoringCommandService = monitoringCommandService;
        this.monitoringQueryService = monitoringQueryService;
    }

    @PostMapping
    @Operation(summary = "Create an employee", description = "Adds a new employee to a parking facility.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<EmployeeResource> createEmployee(@Valid @RequestBody CreateEmployeeResource resource) {
        var command = new CreateEmployeeCommand(
                resource.parkingId(), resource.firstName(), resource.lastName(),
                EmployeeRole.fromDisplayName(resource.role()), resource.schedule(),
                resource.shiftStart(), resource.shiftEnd(), EmployeeStatus.fromDisplayName(resource.status()));
        var employeeOptional = monitoringCommandService.handle(command);
        if (employeeOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(toResource(employeeOptional.get()), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all employees", description = "Returns the full list of employees.")
    @ApiResponse(responseCode = "200", description = "List of employees returned",
            content = @Content(schema = @Schema(implementation = EmployeeResource.class)))
    public ResponseEntity<List<EmployeeResource>> getAllEmployees() {
        var employees = monitoringQueryService.handle(new GetAllEmployeesQuery());
        var resources = employees.stream().map(this::toResource).toList();
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update an employee", description = "Replaces an employee's data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or employee not found")
    })
    public ResponseEntity<EmployeeResource> updateEmployee(
            @PathVariable String employeeId,
            @Valid @RequestBody UpdateEmployeeResource resource) {
        var command = new UpdateEmployeeCommand(
                employeeId, resource.firstName(), resource.lastName(),
                EmployeeRole.fromDisplayName(resource.role()), resource.schedule(),
                resource.shiftStart(), resource.shiftEnd(), EmployeeStatus.fromDisplayName(resource.status()));
        var employeeOptional = monitoringCommandService.handle(command);
        if (employeeOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(toResource(employeeOptional.get()));
    }

    @DeleteMapping("/{employeeId}")
    @Operation(summary = "Delete an employee", description = "Removes an employee by its ID.")
    @ApiResponse(responseCode = "204", description = "Employee deleted successfully")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String employeeId) {
        monitoringCommandService.handle(new DeleteEmployeeCommand(employeeId));
        return ResponseEntity.noContent().build();
    }

    private EmployeeResource toResource(Employee employee) {
        return new EmployeeResource(
                employee.getId(),
                employee.getParkingId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getRole().getDisplayName(),
                employee.getSchedule(),
                employee.getShiftStart(),
                employee.getShiftEnd(),
                employee.getStatus().getDisplayName()
        );
    }
}
