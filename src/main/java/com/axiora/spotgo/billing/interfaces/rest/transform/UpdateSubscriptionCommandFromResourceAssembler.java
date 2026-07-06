package com.axiora.spotgo.billing.interfaces.rest.transform;

import com.axiora.spotgo.billing.domain.model.commands.UpdateSubscriptionCommand;
import com.axiora.spotgo.billing.domain.model.valueobjects.SubscriptionStatus;
import com.axiora.spotgo.billing.interfaces.rest.resources.UpdateSubscriptionResource;

public class UpdateSubscriptionCommandFromResourceAssembler {

    public static UpdateSubscriptionCommand toCommandFromResource(String subscriptionId,
                                                                   UpdateSubscriptionResource resource) {
        return new UpdateSubscriptionCommand(
                subscriptionId,
                resource.planId(),
                SubscriptionStatus.fromValue(resource.status()),
                resource.renewsOn(),
                resource.pricePerMonth(),
                resource.sessions(),
                resource.savedThisMonth(),
                resource.savingsMonth(),
                resource.autoRenewal(),
                resource.paymentMethodLastFour(),
                resource.paymentMethodExpiry()
        );
    }
}
