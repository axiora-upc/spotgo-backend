package com.axiora.spotgo.billing.domain.model.commands;

public record PatchSubscriptionSavingsCommand(
        Long subscriptionId,
        Double savedThisMonth,
        String savingsMonth
) {}
