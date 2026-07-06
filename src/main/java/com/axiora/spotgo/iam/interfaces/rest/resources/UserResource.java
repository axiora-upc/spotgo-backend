package com.axiora.spotgo.iam.interfaces.rest.resources;

public record UserResource(
        String id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String city,
        String role,
        String parkingId,
        String parkingName
) {
}
