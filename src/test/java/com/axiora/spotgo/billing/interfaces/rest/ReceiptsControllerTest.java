package com.axiora.spotgo.billing.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.axiora.spotgo.billing.application.commandservices.ReceiptCommandService;
import com.axiora.spotgo.billing.application.queryservices.ReceiptQueryService;
import com.axiora.spotgo.billing.domain.model.aggregates.Receipt;
import com.axiora.spotgo.billing.domain.model.commands.CreateReceiptCommand;
import com.axiora.spotgo.billing.domain.model.queries.GetReceiptByIdQuery;
import com.axiora.spotgo.billing.domain.repositories.ReceiptRepository;
import com.axiora.spotgo.billing.domain.model.valueobjects.ReceiptStatus;
import com.axiora.spotgo.billing.interfaces.rest.resources.CreateReceiptResource;
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
class ReceiptsControllerTest {

    @Mock
    private ReceiptCommandService receiptCommandService;
    @Mock
    private ReceiptQueryService receiptQueryService;
    @Mock
    private ReceiptRepository receiptRepository;

    private ReceiptsController controller;

    @BeforeEach
    void setUp() {
        controller = new ReceiptsController(receiptCommandService, receiptQueryService, receiptRepository);
    }

    @Test
    void createReceiptUsesAuthenticatedClientId() {
        var principal = new SpotgoUserPrincipal("client-auth", "client@spotgo.com", "pw", UserRole.CLIENT);
        var resource = new CreateReceiptResource(
                "body-client",
                "INV-001",
                "SpotGo Center",
                "2026-07-08",
                1,
                30,
                "Visa 4242",
                "SPG-001",
                10.0);
        when(receiptCommandService.handle(any(CreateReceiptCommand.class))).thenReturn(Result.success(
                new Receipt("receipt-1", "client-auth", "INV-001", "SpotGo Center", "2026-07-08", 1, 30, "Visa 4242", "SPG-001", 10.0, ReceiptStatus.PENDING)));

        controller.createReceipt(principal, resource);

        var captor = ArgumentCaptor.forClass(CreateReceiptCommand.class);
        verify(receiptCommandService).handle(captor.capture());
        assertEquals("client-auth", captor.getValue().clientId());
    }

    @Test
    void getAllReceiptsReturnsOnlyAuthenticatedClientReceipts() {
        var principal = new SpotgoUserPrincipal("client-auth", "client@spotgo.com", "pw", UserRole.CLIENT);
        when(receiptRepository.findAllByClientId("client-auth")).thenReturn(List.of(
                new Receipt("receipt-1", "client-auth", "INV-001", "SpotGo Center", "2026-07-08", 1, 30, "Visa 4242", "SPG-001", 10.0, ReceiptStatus.PENDING)));

        var response = controller.getAllReceipts(principal, null);

        assertEquals(1, response.getBody().size());
        assertEquals("client-auth", response.getBody().get(0).clientId());
    }

    @Test
    void getReceiptByIdRejectsForeignReceipt() {
        var principal = new SpotgoUserPrincipal("client-auth", "client@spotgo.com", "pw", UserRole.CLIENT);
        when(receiptQueryService.handle(any(GetReceiptByIdQuery.class))).thenReturn(Optional.of(
                new Receipt("receipt-1", "other-client", "INV-001", "SpotGo Center", "2026-07-08", 1, 30, "Visa 4242", "SPG-001", 10.0, ReceiptStatus.PENDING)));

        assertThrows(AccessDeniedException.class, () -> controller.getReceiptById(principal, "receipt-1"));
    }
}
