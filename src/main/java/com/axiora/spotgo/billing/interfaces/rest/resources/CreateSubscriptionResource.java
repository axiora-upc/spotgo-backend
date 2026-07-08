package com.axiora.spotgo.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSubscriptionResource(
        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Client identifier", example = "1")
        String clientId,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Plan identifier", example = "2")
        String planId,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Next renewal date", example = "2026-07-16")
        String renewsOn,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Monthly price in S/.", example = "29.99")
        Double pricePerMonth,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Member since date", example = "2025-01-01")
        String memberSince,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Auto-renewal enabled", example = "true")
        Boolean autoRenewal,

        @Schema(description = "Last four digits of payment method", example = "4242")
        String paymentMethodLastFour,

        @Schema(description = "Payment method expiry", example = "12/28")
        String paymentMethodExpiry
) {
}
