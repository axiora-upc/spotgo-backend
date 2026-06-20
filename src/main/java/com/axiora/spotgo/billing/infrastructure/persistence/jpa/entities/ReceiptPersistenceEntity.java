package com.axiora.spotgo.billing.infrastructure.persistence.jpa.entities;

import com.axiora.spotgo.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "receipts")
public class ReceiptPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private String locationName;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private Integer durationHours;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false, unique = true)
    private String bookingCode;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status;

    public ReceiptPersistenceEntity() {
    }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
