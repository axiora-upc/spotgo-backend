package com.axiora.spotgo.billing.domain.model.aggregates;

import com.axiora.spotgo.billing.domain.model.commands.CreateReceiptCommand;
import com.axiora.spotgo.billing.domain.model.valueobjects.ReceiptStatus;
import com.axiora.spotgo.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

public class Receipt extends AbstractDomainAggregateRoot<Receipt> {

    @Getter
    @Setter
    private String id;

    @Getter
    private String clientId;

    @Getter
    private String invoiceNumber;

    @Getter
    private String locationName;

    @Getter
    private String date;

    @Getter
    private Integer durationHours;

    @Getter
    private Integer durationMinutes;

    @Getter
    private String paymentMethod;

    @Getter
    private String bookingCode;

    @Getter
    private Double amount;

    @Getter
    private ReceiptStatus status;

    public Receipt(String id, String clientId, String invoiceNumber, String locationName,
                   String date, Integer durationHours, Integer durationMinutes,
                   String paymentMethod, String bookingCode, Double amount, ReceiptStatus status) {
        this.id = id;
        this.clientId = clientId;
        this.invoiceNumber = invoiceNumber;
        this.locationName = locationName;
        this.date = date;
        this.durationHours = durationHours;
        this.durationMinutes = durationMinutes;
        this.paymentMethod = paymentMethod;
        this.bookingCode = bookingCode;
        this.amount = amount;
        this.status = status;
    }

    public Receipt(String clientId, String invoiceNumber, String locationName,
                   String date, Integer durationHours, Integer durationMinutes,
                   String paymentMethod, String bookingCode, Double amount, ReceiptStatus status) {
        this(null, clientId, invoiceNumber, locationName, date, durationHours,
                durationMinutes, paymentMethod, bookingCode, amount, status);
    }

    public Receipt(CreateReceiptCommand command) {
        this(command.clientId(), command.invoiceNumber(), command.locationName(),
                command.date(), command.durationHours(), command.durationMinutes(),
                command.paymentMethod(), command.bookingCode(), command.amount(),
                ReceiptStatus.PENDING);
    }
}
