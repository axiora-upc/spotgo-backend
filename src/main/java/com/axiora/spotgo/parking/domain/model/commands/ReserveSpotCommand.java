package com.axiora.spotgo.parking.domain.model.commands;

import java.time.LocalDateTime;

public record ReserveSpotCommand(
        String clientId,
        String parkingId,
        String code,
        String spot,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Double amount,
        Double baseAmount,
        Double rating
) {
}
