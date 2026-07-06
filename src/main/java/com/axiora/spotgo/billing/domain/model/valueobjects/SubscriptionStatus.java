package com.axiora.spotgo.billing.domain.model.valueobjects;

public enum SubscriptionStatus {
    ACTIVE,
    INACTIVE,
    CANCELLED;

    public static SubscriptionStatus fromValue(String value) {
        try {
            return SubscriptionStatus.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid subscription status: " + value);
        }
    }
}
