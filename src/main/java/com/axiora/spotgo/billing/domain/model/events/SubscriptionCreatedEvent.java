package com.axiora.spotgo.billing.domain.model.events;

import com.axiora.spotgo.billing.domain.model.aggregates.Subscription;

public record SubscriptionCreatedEvent(
        Long subscriptionId,
        Long clientId,
        Long planId,
        String status
) {
    public static SubscriptionCreatedEvent from(Subscription subscription) {
        return new SubscriptionCreatedEvent(
                subscription.getId(),
                subscription.getClientId(),
                subscription.getPlanId(),
                subscription.getStatus().name()
        );
    }
}
