package com.axiora.spotgo.billing.interfaces.rest.transform;

import com.axiora.spotgo.billing.domain.model.commands.CreateSubscriptionCommand;
import com.axiora.spotgo.billing.interfaces.rest.resources.CreateSubscriptionResource;

public class CreateSubscriptionCommandFromResourceAssembler {

    public static CreateSubscriptionCommand toCommandFromResource(CreateSubscriptionResource resource) {
        return new CreateSubscriptionCommand(
                resource.clientId(),
                resource.planId(),
                resource.renewsOn(),
                resource.pricePerMonth(),
                resource.memberSince(),
                resource.autoRenewal(),
                resource.paymentMethodLastFour(),
                resource.paymentMethodExpiry()
        );
    }
}
