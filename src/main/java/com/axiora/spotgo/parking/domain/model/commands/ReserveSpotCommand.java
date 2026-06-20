package com.axiora.spotgo.parking.domain.model.commands;

import java.time.LocalDateTime;

public record ReserveSpotCommand(
        Long clientId,
        Long parkingId,
        String code,
        String spot,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Double amount,
        Double baseAmount,
        Double rating
) {
}
