package com.axiora.spotgo.parking.domain.model.commands;

public record CreateParkingCommand(
        Long adminId, String name, String address, String city,
        Integer totalSpaces, Integer availableSpaces, Integer totalFloors,
        Double averageOccupancy, Double occupancyTrendPercent, String peakHour,
        Double totalRevenue, String systemStatus, Double rating, Double pricePerHour,
        Double revenueTrendPercent, Integer totalCapacity, Double efficiencyIndex
) {
}
