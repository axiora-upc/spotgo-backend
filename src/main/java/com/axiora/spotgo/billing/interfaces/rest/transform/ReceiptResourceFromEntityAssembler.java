package com.axiora.spotgo.billing.interfaces.rest.transform;

import com.axiora.spotgo.billing.domain.model.aggregates.Receipt;
import com.axiora.spotgo.billing.interfaces.rest.resources.ReceiptResource;

public class ReceiptResourceFromEntityAssembler {

    public static ReceiptResource toResourceFromEntity(Receipt entity) {
        return new ReceiptResource(
                entity.getId(),
                entity.getClientId(),
                entity.getInvoiceNumber(),
                entity.getLocationName(),
                entity.getDate(),
                entity.getDurationHours(),
                entity.getDurationMinutes(),
                entity.getPaymentMethod(),
                entity.getBookingCode(),
                entity.getAmount(),
                entity.getStatus().name().toLowerCase()
        );
    }
}
