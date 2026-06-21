package com.axiora.spotgo.billing.interfaces.rest.transform;

import com.axiora.spotgo.billing.domain.model.aggregates.ClientPlan;
import com.axiora.spotgo.billing.interfaces.rest.resources.ClientPlanResource;

public class ClientPlanResourceFromEntityAssembler {

    public static ClientPlanResource toResourceFromEntity(ClientPlan entity) {
        return new ClientPlanResource(
                entity.getId(),
                entity.getType().name().toLowerCase(),
                entity.getName(),
                entity.getMonthlyPrice(),
                entity.getDescription(),
                entity.getReservationsPerMonth(),
                entity.getDiscountPercent(),
                entity.getFeatures()
        );
    }
}
