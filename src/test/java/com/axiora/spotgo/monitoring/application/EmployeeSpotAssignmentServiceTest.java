package com.axiora.spotgo.monitoring.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.axiora.spotgo.monitoring.domain.model.aggregates.Employee;
import com.axiora.spotgo.monitoring.domain.model.valueobjects.EmployeeRole;
import com.axiora.spotgo.monitoring.domain.model.valueobjects.EmployeeStatus;
import com.axiora.spotgo.monitoring.infrastructure.persistence.jpa.repositories.EmployeeRepository;
import com.axiora.spotgo.parking.domain.model.aggregates.DetectedSpot;
import com.axiora.spotgo.parking.domain.model.aggregates.Reservation;
import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;
import com.axiora.spotgo.parking.domain.model.valueobjects.ReservationStatus;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.DetectedSpotRepository;
import com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeSpotAssignmentServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DetectedSpotRepository detectedSpotRepository;
    @Mock
    private ReservationRepository reservationRepository;

    private EmployeeSpotAssignmentService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeSpotAssignmentService(employeeRepository, detectedSpotRepository, reservationRepository);
    }

    @Test
    void validateAssignmentRejectsOnDutyEmployeeWhenSpotHasConflictingReservation() {
        var spotA1 = new DetectedSpot(1, "bp-1", "parking-1", 0, 0, 0.1, 0.1, 0.1, 0.1, SpotStatus.AVAILABLE);
        var reservation = new Reservation(
                "client-1",
                "parking-1",
                "SPG-001",
                "A1",
                LocalDateTime.of(2026, 7, 9, 10, 0),
                LocalDateTime.of(2026, 7, 9, 11, 0),
                10.0,
                10.0,
                null);
        when(detectedSpotRepository.findByParkingId("parking-1")).thenReturn(List.of(spotA1));
        when(employeeRepository.findByParkingIdAndAssignedSpot("parking-1", "A1")).thenReturn(Optional.empty());
        when(reservationRepository.findByParkingIdAndSpot("parking-1", "A1")).thenReturn(List.of(reservation));

        var exception = assertThrows(IllegalArgumentException.class, () -> service.validateAssignment(
                "parking-1",
                null,
                "a1",
                "09:00",
                "17:00",
                EmployeeStatus.ON_DUTY));

        assertEquals("The assigned spot already has a reservation during the employee shift. Assign another spot before setting the employee on-duty.", exception.getMessage());
    }

    @Test
    void validateAssignmentIgnoresCompletedReservation() {
        var spotA1 = new DetectedSpot(1, "bp-1", "parking-1", 0, 0, 0.1, 0.1, 0.1, 0.1, SpotStatus.AVAILABLE);
        var reservation = new Reservation(
                "client-1", "parking-1", "SPG-001", "A1",
                LocalDateTime.of(2026, 7, 9, 10, 0), LocalDateTime.of(2026, 7, 9, 11, 0),
                10.0, 10.0, null);
        reservation.updateStatus(ReservationStatus.COMPLETED);
        when(detectedSpotRepository.findByParkingId("parking-1")).thenReturn(List.of(spotA1));
        when(employeeRepository.findByParkingIdAndAssignedSpot("parking-1", "A1")).thenReturn(Optional.empty());
        when(reservationRepository.findByParkingIdAndSpot("parking-1", "A1")).thenReturn(List.of(reservation));

        assertDoesNotThrow(() -> service.validateAssignment(
                "parking-1", null, "A1", "09:00", "17:00", EmployeeStatus.ON_DUTY));
    }

    @Test
    void reservedEmployeesBySpotIncludesOnlyOnDutyEmployeesInsideShift() {
        var onDutyEmployee = new Employee(
                "parking-1",
                "Ana",
                "Diaz",
                EmployeeRole.GUARD,
                "all-week",
                "09:00",
                "17:00",
                "A1",
                EmployeeStatus.ON_DUTY);
        var offDutyEmployee = new Employee(
                "parking-1",
                "Luis",
                "Perez",
                EmployeeRole.GUARD,
                "all-week",
                "09:00",
                "17:00",
                "A2",
                EmployeeStatus.OFF_DUTY);
        when(employeeRepository.findByParkingId("parking-1")).thenReturn(List.of(onDutyEmployee, offDutyEmployee));

        var reservedBySpot = service.getReservedEmployeesBySpot("parking-1", LocalDateTime.of(2026, 7, 9, 10, 0));

        assertEquals(1, reservedBySpot.size());
        assertTrue(reservedBySpot.containsKey("A1"));
        assertEquals("Ana", reservedBySpot.get("A1").getFirstName());
    }
}
