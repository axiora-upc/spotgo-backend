package com.axiora.spotgo.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSubscriptionResource(
        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Plan identifier", example = "2")
        Long planId,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Subscription status: ACTIVE, INACTIVE or CANCELLED", example = "ACTIVE")
        String status,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Next renewal date", example = "2026-07-16")
        String renewsOn,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Monthly price in S/.", example = "29.99")
        Double pricePerMonth,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Sessions used this month", example = "5")
        Integer sessions,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Amount saved this month in S/.", example = "10.00")
        Double savedThisMonth,

        @Schema(description = "Month label for savings", example = "June 2026")
        String savingsMonth,

        @NotNull(message = "{validation.not-null}")
        @Schema(description = "Auto-renewal enabled", example = "true")
        Boolean autoRenewal,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Last four digits of payment method", example = "4242")
        String paymentMethodLastFour,

        @NotBlank(message = "{validation.not-blank}")
        @Schema(description = "Payment method expiry", example = "12/28")
        String paymentMethodExpiry
) {
}
