package com.axiora.spotgo.billing.application.internal.commandservices;

import com.axiora.spotgo.billing.application.commandservices.SubscriptionCommandService;
import com.axiora.spotgo.billing.domain.model.aggregates.Subscription;
import com.axiora.spotgo.billing.domain.model.commands.CreateSubscriptionCommand;
import com.axiora.spotgo.billing.domain.model.commands.PatchSubscriptionSavingsCommand;
import com.axiora.spotgo.billing.domain.model.commands.UpdateSubscriptionCommand;
import com.axiora.spotgo.billing.domain.repositories.SubscriptionRepository;
import com.axiora.spotgo.shared.application.result.ApplicationError;
import com.axiora.spotgo.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Result<Subscription, ApplicationError> handle(CreateSubscriptionCommand command) {
        try {
            var subscription = new Subscription(command);
            var saved = subscriptionRepository.save(subscription);
            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Subscription", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Subscription creation", e.getMessage()));
        }
    }

    @Override
    public Result<Subscription, ApplicationError> handle(UpdateSubscriptionCommand command) {
        try {
            var existing = subscriptionRepository.findById(command.subscriptionId());
            if (existing.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "Subscription", "Subscription with ID %d not found".formatted(command.subscriptionId())));
            }
            var subscription = existing.get();
            subscription.update(command);
            var saved = subscriptionRepository.save(subscription);
            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Subscription", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Subscription update", e.getMessage()));
        }
    }

    @Override
    public Result<Subscription, ApplicationError> handle(PatchSubscriptionSavingsCommand command) {
        try {
            var existing = subscriptionRepository.findById(command.subscriptionId());
            if (existing.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "Subscription", "Subscription with ID %d not found".formatted(command.subscriptionId())));
            }
            var subscription = existing.get();
            subscription.patchSavings(command);
            var saved = subscriptionRepository.save(subscription);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Subscription patch savings", e.getMessage()));
        }
    }
}
