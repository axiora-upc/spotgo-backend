package com.axiora.spotgo.billing.domain.repositories;

import com.axiora.spotgo.billing.domain.model.aggregates.ClientPlan;

import java.util.List;
import java.util.Optional;

public interface ClientPlanRepository {

    Optional<ClientPlan> findById(String id);

    List<ClientPlan> findAll();

    ClientPlan save(ClientPlan clientPlan);
}
