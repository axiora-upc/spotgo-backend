package com.axiora.spotgo.billing.domain.repositories;

import com.axiora.spotgo.billing.domain.model.aggregates.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {

    Optional<Subscription> findById(Long id);

    Optional<Subscription> findByClientId(Long clientId);

    List<Subscription> findAll();

    Subscription save(Subscription subscription);
}
