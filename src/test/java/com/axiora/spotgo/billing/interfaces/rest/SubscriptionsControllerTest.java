package com.axiora.spotgo.billing.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.axiora.spotgo.billing.application.commandservices.SubscriptionCommandService;
import com.axiora.spotgo.billing.application.queryservices.SubscriptionQueryService;
import com.axiora.spotgo.billing.domain.model.aggregates.Subscription;
import com.axiora.spotgo.billing.domain.model.commands.CreateSubscriptionCommand;
import com.axiora.spotgo.billing.domain.repositories.SubscriptionRepository;
import com.axiora.spotgo.billing.domain.model.valueobjects.SubscriptionStatus;
import com.axiora.spotgo.billing.interfaces.rest.resources.CreateSubscriptionResource;
import com.axiora.spotgo.billing.interfaces.rest.resources.UpdateSubscriptionResource;
import com.axiora.spotgo.iam.domain.model.valueobjects.UserRole;
import com.axiora.spotgo.iam.infrastructure.security.SpotgoUserPrincipal;
import com.axiora.spotgo.shared.application.result.Result;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class SubscriptionsControllerTest {

    @Mock
    private SubscriptionCommandService subscriptionCommandService;
    @Mock
    private SubscriptionQueryService subscriptionQueryService;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    private SubscriptionsController controller;

    @BeforeEach
    void setUp() {
        controller = new SubscriptionsController(subscriptionCommandService, subscriptionQueryService, subscriptionRepository);
    }

    @Test
    void createSubscriptionUsesAuthenticatedClientId() {
        var principal = new SpotgoUserPrincipal("client-auth", "client@spotgo.com", "pw", UserRole.CLIENT);
        var resource = new CreateSubscriptionResource("body-client", "plan-pro", "2026-08-01", 29.9, "2026-07-01", true, "4242", "12/28");
        when(subscriptionCommandService.handle(any(CreateSubscriptionCommand.class))).thenReturn(Result.success(
                new Subscription("sub-1", "client-auth", "plan-pro", SubscriptionStatus.ACTIVE, "2026-08-01", 29.9, 0, 0.0, "", "2026-07-01", true, "4242", "12/28")));

        controller.createSubscription(principal, resource);

        var captor = ArgumentCaptor.forClass(CreateSubscriptionCommand.class);
        verify(subscriptionCommandService).handle(captor.capture());
        assertEquals("client-auth", captor.getValue().clientId());
    }

    @Test
    void getAllSubscriptionsReturnsOnlyAuthenticatedClientSubscriptions() {
        var principal = new SpotgoUserPrincipal("client-auth", "client@spotgo.com", "pw", UserRole.CLIENT);
        when(subscriptionRepository.findAllByClientId("client-auth")).thenReturn(List.of(
                new Subscription("sub-1", "client-auth", "plan-pro", SubscriptionStatus.ACTIVE, "2026-08-01", 29.9, 0, 0.0, "", "2026-07-01", true, "4242", "12/28")));

        var response = controller.getAllSubscriptions(principal);

        assertEquals(1, response.getBody().size());
        assertEquals("client-auth", response.getBody().get(0).clientId());
    }

    @Test
    void updateSubscriptionRejectsForeignOwner() {
        var principal = new SpotgoUserPrincipal("client-auth", "client@spotgo.com", "pw", UserRole.CLIENT);
        var resource = new UpdateSubscriptionResource("plan-pro", "active", "2026-08-01", 29.9, 1, 3.0, "2026-07", true, "4242", "12/28");
        when(subscriptionRepository.findById("sub-1")).thenReturn(Optional.of(
                new Subscription("sub-1", "other-client", "plan-pro", SubscriptionStatus.ACTIVE, "2026-08-01", 29.9, 0, 0.0, "", "2026-07-01", true, "4242", "12/28")));

        assertThrows(AccessDeniedException.class, () -> controller.updateSubscription(principal, "sub-1", resource));
    }
}
