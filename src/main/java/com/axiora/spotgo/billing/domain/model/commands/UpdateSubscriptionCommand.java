package com.axiora.spotgo.billing.domain.model.commands;

import com.axiora.spotgo.billing.domain.model.valueobjects.SubscriptionStatus;

public record UpdateSubscriptionCommand(
        Long subscriptionId,
        Long planId,
        SubscriptionStatus status,
        String renewsOn,
        Double pricePerMonth,
        Integer sessions,
        Double savedThisMonth,
        String savingsMonth,
        Boolean autoRenewal,
        String paymentMethodLastFour,
        String paymentMethodExpiry
) {
}
