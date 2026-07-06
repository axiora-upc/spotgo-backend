package com.axiora.spotgo.parking.domain.model.valueobjects;

public enum ReservationStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED;

    public static ReservationStatus fromValue(String value) {
        try {
            return ReservationStatus.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid reservation status: " + value);
        }
    }
}
