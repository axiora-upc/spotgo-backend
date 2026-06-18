package com.axiora.spotgo.billing.domain.model.events;

import com.axiora.spotgo.billing.domain.model.aggregates.Receipt;

public record ReceiptCreatedEvent(
        Long receiptId,
        Long clientId,
        String invoiceNumber,
        Double amount,
        String status
) {
    public static ReceiptCreatedEvent from(Receipt receipt) {
        return new ReceiptCreatedEvent(
                receipt.getId(),
                receipt.getClientId(),
                receipt.getInvoiceNumber(),
                receipt.getAmount(),
                receipt.getStatus().name()
        );
    }
}
