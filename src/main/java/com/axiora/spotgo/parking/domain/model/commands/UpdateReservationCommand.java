package com.axiora.spotgo.parking.domain.model.commands;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReservationStatus;

import java.time.LocalDateTime;

public record UpdateReservationCommand(
        String reservationId,
        LocalDateTime endDate,
        Double amount,
        Double baseAmount,
        Double rating,
        ReservationStatus status
) {
}
