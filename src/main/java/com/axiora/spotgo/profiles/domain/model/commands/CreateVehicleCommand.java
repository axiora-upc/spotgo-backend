package com.axiora.spotgo.profiles.domain.model.commands;

public record CreateVehicleCommand(String clientId, String licensePlate, String vehicleType, String brand, String model) {
}
