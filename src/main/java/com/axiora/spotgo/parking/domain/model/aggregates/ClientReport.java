package com.axiora.spotgo.parking.domain.model.aggregates;

import com.axiora.spotgo.parking.domain.model.valueobjects.ReportStatus;
import com.axiora.spotgo.parking.domain.model.valueobjects.ReportType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "clientReports")
@Getter
public class ClientReport extends AbstractAggregateRoot<ClientReport> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clientId", nullable = false)
    private Long clientId;

    @Column(name = "parkingId", nullable = false)
    private Long parkingId;

    @Column(name = "reservationId", nullable = false)
    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @Column(nullable = false)
    private String date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    public ClientReport() {
    }

    // status always starts as SUBMITTED; it only changes later via PATCH
    public ClientReport(Long clientId, Long parkingId, Long reservationId, ReportType type, String date) {
        this.clientId = clientId;
        this.parkingId = parkingId;
        this.reservationId = reservationId;
        this.type = type;
        this.date = date;
        this.status = ReportStatus.SUBMITTED;
    }

    public void updateStatus(ReportStatus newStatus) {
        this.status = newStatus;
    }
}
