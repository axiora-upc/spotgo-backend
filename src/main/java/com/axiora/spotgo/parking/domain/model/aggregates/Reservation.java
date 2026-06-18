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

    @Column(name = "vehicle_plate", nullable = false)
    private String vehiclePlate;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private Double penalty;

    public Reservation() {
    }

    public Reservation(String vehiclePlate, Long spotId, LocalDateTime startTime, LocalDateTime endTime) {
        this.vehiclePlate = vehiclePlate;
        this.spotId = spotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ReservationStatus.ACTIVE;
        this.penalty = 0.0;
    }

    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }

    public void addPenalty(Double amount) {
        this.penalty += amount;
    }
}
