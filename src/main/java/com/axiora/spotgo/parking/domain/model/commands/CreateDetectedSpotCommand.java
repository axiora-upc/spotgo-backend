package com.axiora.spotgo.parking.domain.model.commands;

import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;

public record CreateDetectedSpotCommand(
        Integer localId,
        Long blueprintId,
        Long parkingId,
        Integer row,
        Integer col,
        Double xPct,
        Double yPct,
        Double wPct,
        Double hPct,
        SpotStatus status
) {
}
