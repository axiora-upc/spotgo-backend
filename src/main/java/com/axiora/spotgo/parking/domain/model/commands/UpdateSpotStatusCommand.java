package com.axiora.spotgo.parking.domain.model.commands;

import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;

public record UpdateSpotStatusCommand(String spotId, SpotStatus status) {
}
