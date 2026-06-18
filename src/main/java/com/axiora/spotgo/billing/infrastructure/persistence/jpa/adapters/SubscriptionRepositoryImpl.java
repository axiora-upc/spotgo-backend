package com.axiora.spotgo.billing.infrastructure.persistence.jpa.adapters;

import com.axiora.spotgo.billing.domain.model.aggregates.Subscription;
import com.axiora.spotgo.billing.domain.repositories.SubscriptionRepository;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final SubscriptionPersistenceRepository subscriptionPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SubscriptionRepositoryImpl(SubscriptionPersistenceRepository subscriptionPersistenceRepository,
                                      ApplicationEventPublisher eventPublisher) {
        this.subscriptionPersistenceRepository = subscriptionPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Subscription> findById(Long id) {
        return subscriptionPersistenceRepository.findById(id)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Subscription> findByClientId(Long clientId) {
        return subscriptionPersistenceRepository.findByClientId(clientId)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Subscription> findAll() {
        return subscriptionPersistenceRepository.findAll().stream()
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Subscription save(Subscription subscription) {
        boolean isNew = subscription.getId() == null;
        var entity = SubscriptionPersistenceAssembler.toPersistenceFromDomain(subscription);
        var saved = subscriptionPersistenceRepository.save(entity);
        var savedSubscription = SubscriptionPersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedSubscription.onCreated();
            savedSubscription.domainEvents().forEach(eventPublisher::publishEvent);
            savedSubscription.clearDomainEvents();
        }
        return savedSubscription;
    }
}
