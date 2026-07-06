package com.axiora.spotgo.profiles.domain.model.commands;

public record CreateFavoriteCommand(String clientId, String parkingId, Double distanceMi, String lastVisited) {
}
