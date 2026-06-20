package com.axiora.spotgo.parking.domain.model.commands;

public record CreateBlueprintCommand(Long adminId, Long parkingId, String name, String dataUrl) {
}
