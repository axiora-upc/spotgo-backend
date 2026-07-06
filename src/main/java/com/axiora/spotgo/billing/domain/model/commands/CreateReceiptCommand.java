package com.axiora.spotgo.billing.domain.model.commands;

public record CreateReceiptCommand(
        String clientId,
        String invoiceNumber,
        String locationName,
        String date,
        Integer durationHours,
        Integer durationMinutes,
        String paymentMethod,
        String bookingCode,
        Double amount
) {
}
