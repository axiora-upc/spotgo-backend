package com.axiora.spotgo.billing.infrastructure.persistence.jpa.assemblers;

import com.axiora.spotgo.billing.domain.model.aggregates.ClientPlan;
import com.axiora.spotgo.billing.domain.model.valueobjects.PlanType;
import com.axiora.spotgo.billing.infrastructure.persistence.jpa.entities.ClientPlanPersistenceEntity;

public final class ClientPlanPersistenceAssembler {

    private ClientPlanPersistenceAssembler() {
    }

    public static ClientPlan toDomainFromPersistence(ClientPlanPersistenceEntity entity) {
        return new ClientPlan(
                entity.getId(),
                PlanType.valueOf(entity.getType()),
                entity.getName(),
                entity.getMonthlyPrice(),
                entity.getDescription(),
                entity.getReservationsPerMonth(),
                entity.getDiscountPercent(),
                entity.getFeatures()
        );
    }

    public static ClientPlanPersistenceEntity toPersistenceFromDomain(ClientPlan domain) {
        ClientPlanPersistenceEntity entity = new ClientPlanPersistenceEntity();
        entity.setId(domain.getId());
        entity.setType(domain.getType().name());
        entity.setName(domain.getName());
        entity.setMonthlyPrice(domain.getMonthlyPrice());
        entity.setDescription(domain.getDescription());
        entity.setReservationsPerMonth(domain.getReservationsPerMonth());
        entity.setDiscountPercent(domain.getDiscountPercent());
        entity.setFeatures(domain.getFeatures());
        return entity;
    }
}
