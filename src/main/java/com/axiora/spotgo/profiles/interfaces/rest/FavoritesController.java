package com.axiora.spotgo.profiles.interfaces.rest;

import com.axiora.spotgo.profiles.application.ProfilesCommandService;
import com.axiora.spotgo.profiles.application.ProfilesQueryService;
import com.axiora.spotgo.profiles.domain.model.aggregates.Favorite;
import com.axiora.spotgo.profiles.domain.model.commands.CreateFavoriteCommand;
import com.axiora.spotgo.profiles.domain.model.commands.DeleteFavoriteCommand;
import com.axiora.spotgo.profiles.domain.model.queries.GetAllFavoritesQuery;
import com.axiora.spotgo.profiles.domain.model.queries.GetFavoritesByClientIdQuery;
import com.axiora.spotgo.profiles.interfaces.rest.resources.CreateFavoriteResource;
import com.axiora.spotgo.profiles.interfaces.rest.resources.FavoriteResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favorites")
@Tag(name = "Favorites", description = "Endpoints for managing client favorite parking facilities")
public class FavoritesController {

    private final ProfilesCommandService profilesCommandService;
    private final ProfilesQueryService profilesQueryService;

    public FavoritesController(ProfilesCommandService profilesCommandService, ProfilesQueryService profilesQueryService) {
        this.profilesCommandService = profilesCommandService;
        this.profilesQueryService = profilesQueryService;
    }

    @GetMapping
    @Operation(summary = "Get favorites", description = "Returns favorites, optionally filtered by client identifier.")
    @ApiResponse(responseCode = "200", description = "Favorites returned successfully",
            content = @Content(schema = @Schema(implementation = FavoriteResource.class)))
    public ResponseEntity<List<FavoriteResource>> getFavorites(@RequestParam(required = false) String clientId) {
        var favorites = clientId == null
                ? profilesQueryService.handle(new GetAllFavoritesQuery())
                : profilesQueryService.handle(new GetFavoritesByClientIdQuery(clientId));
        return ResponseEntity.ok(favorites.stream().map(this::toResource).toList());
    }

    @PostMapping
    @Operation(summary = "Create favorite", description = "Creates a new favorite parking reference for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Favorite created successfully",
                    content = @Content(schema = @Schema(implementation = FavoriteResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<FavoriteResource> createFavorite(@Valid @RequestBody CreateFavoriteResource resource) {
        var favorite = profilesCommandService.handle(new CreateFavoriteCommand(
                resource.clientId(), resource.parkingId(), resource.distanceMi(), resource.lastVisited()));
        return favorite.map(value -> ResponseEntity.status(HttpStatus.CREATED).body(toResource(value)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{favoriteId}")
    @Operation(summary = "Delete favorite", description = "Deletes a favorite by identifier.")
    @ApiResponse(responseCode = "204", description = "Favorite deleted successfully")
    public ResponseEntity<Void> deleteFavorite(@PathVariable String favoriteId) {
        profilesCommandService.handle(new DeleteFavoriteCommand(favoriteId));
        return ResponseEntity.noContent().build();
    }

    private FavoriteResource toResource(Favorite favorite) {
        return new FavoriteResource(favorite.getId(), favorite.getClientId(), favorite.getParkingId(), favorite.getDistanceMi(), favorite.getLastVisited());
    }
}
