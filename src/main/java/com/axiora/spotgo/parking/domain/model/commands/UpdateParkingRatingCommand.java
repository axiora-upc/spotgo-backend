package com.axiora.spotgo.parking.domain.model.commands;

public record UpdateParkingRatingCommand(Long parkingId, Double rating) {
}
