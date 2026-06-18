package com.axiora.spotgo.billing.infrastructure.persistence.jpa.repositories;

import com.axiora.spotgo.billing.infrastructure.persistence.jpa.entities.ReceiptPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptPersistenceRepository extends JpaRepository<ReceiptPersistenceEntity, Long> {

    List<ReceiptPersistenceEntity> findAllByClientId(Long clientId);

    List<ReceiptPersistenceEntity> findAllByBookingCode(String bookingCode);
}
