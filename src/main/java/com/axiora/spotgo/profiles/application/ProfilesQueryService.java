package com.axiora.spotgo.profiles.application;

import com.axiora.spotgo.profiles.domain.model.aggregates.Favorite;
import com.axiora.spotgo.profiles.domain.model.aggregates.Vehicle;
import com.axiora.spotgo.profiles.domain.model.queries.GetAllFavoritesQuery;
import com.axiora.spotgo.profiles.domain.model.queries.GetAllVehiclesQuery;
import com.axiora.spotgo.profiles.domain.model.queries.GetFavoritesByClientIdQuery;
import com.axiora.spotgo.profiles.domain.model.queries.GetVehiclesByClientIdQuery;

import java.util.List;

public interface ProfilesQueryService {
    List<Vehicle> handle(GetAllVehiclesQuery query);
    List<Vehicle> handle(GetVehiclesByClientIdQuery query);
    List<Favorite> handle(GetAllFavoritesQuery query);
    List<Favorite> handle(GetFavoritesByClientIdQuery query);
}
