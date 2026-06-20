package com.axiora.spotgo.monitoring.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.monitoring.domain.model.aggregates.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
