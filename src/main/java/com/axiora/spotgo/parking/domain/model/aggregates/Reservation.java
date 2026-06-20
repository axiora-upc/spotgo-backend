package com.axiora.spotgo.parking.domain.model.aggregates;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
public class Reservation extends AbstractAggregateRoot<Reservation> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clientId")
    private Long clientId;

    @Column(name = "parkingId")
    private Long parkingId;

    @Column(name = "code")
    private String code;

    @Column(name = "spot")
    private String spot;

    @Column(name = "startDate")
    private LocalDateTime startDate;

    @Column(name = "endDate")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "baseAmount")
    private Double baseAmount;

    @Column(name = "rating")
    private Double rating;

    public Reservation() {
    }

    public Reservation(Long clientId, Long parkingId, String code, String spot,
                       LocalDateTime startDate, LocalDateTime endDate,
                       Double amount, Double baseAmount, Double rating) {
        this.clientId = clientId;
        this.parkingId = parkingId;
        this.code = code;
        this.spot = spot;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.baseAmount = baseAmount;
        this.rating = rating;
        this.status = ReservationStatus.ACTIVE;
    }

    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }
}
