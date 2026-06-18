package com.axiora.spotgo.parking.domain.model.commands;

import com.axiora.spotgo.parking.domain.model.valueobjects.Coordinates;

public record CreateDetectedSpotCommand(Coordinates coordinates, Long blueprintId) {
}
