package com.axiora.spotgo.parking.domain.model.commands;

import java.time.LocalDateTime;

public record ReserveSpotCommand(String vehiclePlate, Long spotId, LocalDateTime startTime, LocalDateTime endTime) {
}
