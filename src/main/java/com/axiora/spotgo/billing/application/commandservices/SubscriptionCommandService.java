package com.axiora.spotgo.billing.application.commandservices;

import com.axiora.spotgo.billing.domain.model.aggregates.Subscription;
import com.axiora.spotgo.billing.domain.model.commands.CreateSubscriptionCommand;
import com.axiora.spotgo.billing.domain.model.commands.PatchSubscriptionSavingsCommand;
import com.axiora.spotgo.billing.domain.model.commands.UpdateSubscriptionCommand;
import com.axiora.spotgo.shared.application.result.ApplicationError;
import com.axiora.spotgo.shared.application.result.Result;

public interface SubscriptionCommandService {

    Result<Subscription, ApplicationError> handle(CreateSubscriptionCommand command);

    Result<Subscription, ApplicationError> handle(UpdateSubscriptionCommand command);

    Result<Subscription, ApplicationError> handle(PatchSubscriptionSavingsCommand command);
}
