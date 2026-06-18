package com.axiora.spotgo.billing.infrastructure.persistence.jpa.adapters;

import com.axiora.spotgo.billing.domain.model.aggregates.Receipt;
import com.axiora.spotgo.billing.domain.repositories.ReceiptRepository;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.assemblers.ReceiptPersistenceAssembler;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.repositories.ReceiptPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReceiptRepositoryImpl implements ReceiptRepository {

    private final ReceiptPersistenceRepository receiptPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReceiptRepositoryImpl(ReceiptPersistenceRepository receiptPersistenceRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.receiptPersistenceRepository = receiptPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Receipt> findById(Long id) {
        return receiptPersistenceRepository.findById(id)
                .map(ReceiptPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Receipt> findAll() {
        return receiptPersistenceRepository.findAll().stream()
                .map(ReceiptPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Receipt> findAllByClientId(Long clientId) {
        return receiptPersistenceRepository.findAllByClientId(clientId).stream()
                .map(ReceiptPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Receipt> findAllByBookingCode(String bookingCode) {
        return receiptPersistenceRepository.findAllByBookingCode(bookingCode).stream()
                .map(ReceiptPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        receiptPersistenceRepository.deleteById(id);
    }

    @Override
    public Receipt save(Receipt receipt) {
        boolean isNew = receipt.getId() == null;
        var entity = ReceiptPersistenceAssembler.toPersistenceFromDomain(receipt);
        var saved = receiptPersistenceRepository.save(entity);
        var savedReceipt = ReceiptPersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedReceipt.onCreated();
            savedReceipt.domainEvents().forEach(eventPublisher::publishEvent);
            savedReceipt.clearDomainEvents();
        }
        return savedReceipt;
    }
}
