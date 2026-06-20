package com.axiora.spotgo.billing.domain.model.commands;

public record CreateSubscriptionCommand(
        Long clientId,
        Long planId,
        String renewsOn,
        Double pricePerMonth,
        String memberSince,
        Boolean autoRenewal,
        String paymentMethodLastFour,
        String paymentMethodExpiry
) {
}
