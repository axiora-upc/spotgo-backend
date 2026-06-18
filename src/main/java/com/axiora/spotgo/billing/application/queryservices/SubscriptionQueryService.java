package com.axiora.spotgo.billing.application.queryservices;

import com.axiora.spotgo.billing.domain.model.aggregates.Subscription;
import com.axiora.spotgo.billing.domain.model.queries.GetAllSubscriptionsQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetSubscriptionByClientIdQuery;
import com.axiora.spotgo.billing.domain.model.queries.GetSubscriptionByIdQuery;

import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryService {

    Optional<Subscription> handle(GetSubscriptionByIdQuery query);

    Optional<Subscription> handle(GetSubscriptionByClientIdQuery query);

    List<Subscription> handle(GetAllSubscriptionsQuery query);
}
