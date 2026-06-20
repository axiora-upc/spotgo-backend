package com.axiora.spotgo.billing.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.billing.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionPersistenceRepository extends JpaRepository<SubscriptionPersistenceEntity, Long> {

    Optional<SubscriptionPersistenceEntity> findByClientId(Long clientId);
}
