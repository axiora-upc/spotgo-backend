package com.axiora.spotgo.billing.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.billing.infrastructure.persistence.jpa.entities.ClientPlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientPlanPersistenceRepository extends JpaRepository<ClientPlanPersistenceEntity, String> {
}
