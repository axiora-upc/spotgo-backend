package com.axiora.spotgo.parking.domain.model.commands;

public record UpdateParkingCommand(String parkingId, Integer totalSpaces, Integer availableSpaces, Integer totalFloors, Double rating) {
}
