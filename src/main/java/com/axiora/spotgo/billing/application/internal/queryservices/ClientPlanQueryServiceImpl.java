package com.axiora.spotgo.billing.application.internal.queryservices;

import com.axiora.spotgo.billing.application.queryservices.ClientPlanQueryService;
import com.axiora.spotgo.billing.domain.model.aggregates.ClientPlan;
import com.axiora.spotgo.billing.domain.model.queries.GetAllClientPlansQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetClientPlanByIdQuery;
import com.axiora.spotgo.billing.domain.repositories.ClientPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientPlanQueryServiceImpl implements ClientPlanQueryService {

    private final ClientPlanRepository clientPlanRepository;

    public ClientPlanQueryServiceImpl(ClientPlanRepository clientPlanRepository) {
        this.clientPlanRepository = clientPlanRepository;
    }

    @Override
    public Optional<ClientPlan> handle(GetClientPlanByIdQuery query) {
        return clientPlanRepository.findById(query.clientPlanId());
    }

    @Override
    public List<ClientPlan> handle(GetAllClientPlansQuery query) {
        return clientPlanRepository.findAll();
    }
}
