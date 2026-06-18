package com.axiora.spotgo.parking.domain.model.commands;

public record CreateParkingCommand(String name, String location, Integer totalSpots) {
}
