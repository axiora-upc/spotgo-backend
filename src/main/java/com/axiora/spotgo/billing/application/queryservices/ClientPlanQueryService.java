package com.axiora.spotgo.billing.application.queryservices;

import com.axiora.spotgo.billing.domain.model.aggregates.ClientPlan;
import com.axiora.spotgo.billing.domain.model.queries.GetAllClientPlansQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetClientPlanByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ClientPlanQueryService {

    Optional<ClientPlan> handle(GetClientPlanByIdQuery query);

    List<ClientPlan> handle(GetAllClientPlansQuery query);
}
