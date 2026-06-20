package com.axiora.spotgo.billing.domain.model.aggregates;

import com.axiora.spotgo.billing.domain.model.commands.CreateSubscriptionCommand;
import com.axiora.spotgo.billing.domain.model.commands.PatchSubscriptionSavingsCommand;
import com.axiora.spotgo.billing.domain.model.commands.UpdateSubscriptionCommand;
import com.axiora.spotgo.billing.domain.model.events.SubscriptionCreatedEvent;
import com.axiora.spotgo.billing.domain.model.valueobjects.SubscriptionStatus;
import com.axiora.spotgo.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

public class Subscription extends AbstractDomainAggregateRoot<Subscription> {

    @Getter
    @Setter
    private Long id;

    @Getter
    private Long clientId;

    @Getter
    @Setter
    private Long planId;

    @Getter
    @Setter
    private SubscriptionStatus status;

    @Getter
    @Setter
    private String renewsOn;

    @Getter
    @Setter
    private Double pricePerMonth;

    @Getter
    @Setter
    private Integer sessions;

    @Getter
    @Setter
    private Double savedThisMonth;

    @Getter
    @Setter
    private String savingsMonth;

    @Getter
    private String memberSince;

    @Getter
    @Setter
    private Boolean autoRenewal;

    @Getter
    @Setter
    private String paymentMethodLastFour;

    @Getter
    @Setter
    private String paymentMethodExpiry;

    public Subscription(Long id, Long clientId, Long planId, SubscriptionStatus status,
                        String renewsOn, Double pricePerMonth, Integer sessions,
                        Double savedThisMonth, String savingsMonth, String memberSince,
                        Boolean autoRenewal, String paymentMethodLastFour, String paymentMethodExpiry) {
        this.id = id;
        this.clientId = clientId;
        this.planId = planId;
        this.status = status;
        this.renewsOn = renewsOn;
        this.pricePerMonth = pricePerMonth;
        this.sessions = sessions;
        this.savedThisMonth = savedThisMonth;
        this.savingsMonth = savingsMonth;
        this.memberSince = memberSince;
        this.autoRenewal = autoRenewal;
        this.paymentMethodLastFour = paymentMethodLastFour;
        this.paymentMethodExpiry = paymentMethodExpiry;
    }

    public Subscription(Long clientId, Long planId, SubscriptionStatus status,
                        String renewsOn, Double pricePerMonth, Integer sessions,
                        Double savedThisMonth, String savingsMonth, String memberSince,
                        Boolean autoRenewal, String paymentMethodLastFour, String paymentMethodExpiry) {
        this(null, clientId, planId, status, renewsOn, pricePerMonth, sessions,
                savedThisMonth, savingsMonth, memberSince, autoRenewal, paymentMethodLastFour, paymentMethodExpiry);
    }

    public Subscription(CreateSubscriptionCommand command) {
        this(command.clientId(), command.planId(), SubscriptionStatus.ACTIVE,
                command.renewsOn(), command.pricePerMonth(), 0,
                0.0, "", command.memberSince(), command.autoRenewal(),
                command.paymentMethodLastFour(), command.paymentMethodExpiry());
    }

    public void update(UpdateSubscriptionCommand command) {
        this.planId = command.planId();
        this.status = command.status();
        this.renewsOn = command.renewsOn();
        this.pricePerMonth = command.pricePerMonth();
        this.sessions = command.sessions();
        this.savedThisMonth = command.savedThisMonth();
        this.savingsMonth = command.savingsMonth();
        this.autoRenewal = command.autoRenewal();
        this.paymentMethodLastFour = command.paymentMethodLastFour();
        this.paymentMethodExpiry = command.paymentMethodExpiry();
    }

    public void patchSavings(PatchSubscriptionSavingsCommand command) {
        this.savedThisMonth = command.savedThisMonth();
        this.savingsMonth = command.savingsMonth();
    }

    public void onCreated() {
        registerDomainEvent(SubscriptionCreatedEvent.from(this));
    }
}
