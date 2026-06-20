package com.axiora.spotgo.monitoring.domain.model.aggregates;

import com.axiora.spotgo.monitoring.domain.model.valueobjects.EmployeeRole;
import com.axiora.spotgo.monitoring.domain.model.valueobjects.EmployeeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "employees")
@Getter
public class Employee extends AbstractAggregateRoot<Employee> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parkingId")
    private Long parkingId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role;

    @Column(nullable = false)
    private String schedule;

    @Column(nullable = false)
    private String shiftStart;

    @Column(nullable = false)
    private String shiftEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;

    public Employee() {
    }

    public Employee(Long parkingId, String firstName, String lastName, EmployeeRole role,
                    String schedule, String shiftStart, String shiftEnd, EmployeeStatus status) {
        this.parkingId = parkingId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.schedule = schedule;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.status = status;
    }

    public void update(String firstName, String lastName, EmployeeRole role,
                       String schedule, String shiftStart, String shiftEnd, EmployeeStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.schedule = schedule;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.status = status;
    }
}
