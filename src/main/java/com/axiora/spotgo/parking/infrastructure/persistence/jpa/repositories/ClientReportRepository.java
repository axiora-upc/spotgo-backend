package com.axiora.spotgo.parking.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.parking.domain.model.aggregates.ClientReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientReportRepository extends JpaRepository<ClientReport, Long> {
}
