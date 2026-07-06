package com.axiora.spotgo.iam.interfaces.rest;

import com.axiora.spotgo.iam.application.UserAccountService;
import com.axiora.spotgo.iam.interfaces.rest.resources.UpdatePasswordResource;
import com.axiora.spotgo.iam.interfaces.rest.resources.UpdateUserResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for managing application users")
public class UsersController {

    private final UserAccountService userAccountService;

    public UsersController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns the full list of users.")
    @ApiResponse(responseCode = "200", description = "Users returned successfully")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userAccountService.getAllUsers());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id", description = "Returns a user by identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User returned successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userAccountService.getUserById(userId));
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update user", description = "Updates mutable user profile fields.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<?> updateUser(
            @PathVariable @Parameter(description = "User identifier") String userId,
            @Valid @RequestBody UpdateUserResource request) {
        return ResponseEntity.ok(userAccountService.updateUser(userId, request.firstName(), request.lastName(), request.phone(), request.city()));
    }

    @PatchMapping("/{userId}/password")
    @Operation(summary = "Update password", description = "Changes a user's password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    public ResponseEntity<Void> updatePassword(
            @PathVariable @Parameter(description = "User identifier") String userId,
            @Valid @RequestBody UpdatePasswordResource request) {
        userAccountService.updatePassword(userId, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
