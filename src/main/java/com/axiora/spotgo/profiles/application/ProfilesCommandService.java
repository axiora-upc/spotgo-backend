package com.axiora.spotgo.profiles.application;

import com.axiora.spotgo.profiles.domain.model.aggregates.Favorite;
import com.axiora.spotgo.profiles.domain.model.aggregates.Vehicle;
import com.axiora.spotgo.profiles.domain.model.commands.CreateFavoriteCommand;
import com.axiora.spotgo.profiles.domain.model.commands.CreateVehicleCommand;
import com.axiora.spotgo.profiles.domain.model.commands.DeleteFavoriteCommand;
import com.axiora.spotgo.profiles.domain.model.commands.DeleteVehicleCommand;
import com.axiora.spotgo.profiles.domain.model.commands.UpdateVehicleCommand;

import java.util.Optional;

public interface ProfilesCommandService {
    Optional<Vehicle> handle(CreateVehicleCommand command);
    Optional<Vehicle> handle(UpdateVehicleCommand command);
    void handle(DeleteVehicleCommand command);
    Optional<Favorite> handle(CreateFavoriteCommand command);
    void handle(DeleteFavoriteCommand command);
}
