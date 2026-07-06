package com.axiora.spotgo.billing.domain.model.commands;

public record CreateSubscriptionCommand(
        String clientId,
        String planId,
        String renewsOn,
        Double pricePerMonth,
        String memberSince,
        Boolean autoRenewal,
        String paymentMethodLastFour,
        String paymentMethodExpiry
) {
}
