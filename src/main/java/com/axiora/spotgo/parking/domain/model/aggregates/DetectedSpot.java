package com.axiora.spotgo.parking.domain.model.aggregates;

import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;
import com.axiora.spotgo.shared.infrastructure.persistence.jpa.entities.UuidIdentifiedAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "detectedSpots")
@Getter
public class DetectedSpot extends UuidIdentifiedAggregateRoot<DetectedSpot> {

    @Column(name = "code")
    private Integer code;

    @Column(name = "blueprintId", nullable = false)
    private String blueprintId;

    @Column(name = "parkingId")
    private String parkingId;

    @Column(name = "rowNum")
    private Integer row;

    @Column(name = "colNum")
    private Integer col;

    @Column(name = "x_pct")
    private Double xPct;

    @Column(name = "y_pct")
    private Double yPct;

    @Column(name = "w_pct")
    private Double wPct;

    @Column(name = "h_pct")
    private Double hPct;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotStatus status;

    public DetectedSpot() {
    }

    public DetectedSpot(Integer code, String blueprintId, String parkingId, Integer row, Integer col,
                         Double xPct, Double yPct, Double wPct, Double hPct, SpotStatus status) {
        this.code = code;
        this.blueprintId = blueprintId;
        this.parkingId = parkingId;
        this.row = row;
        this.col = col;
        this.xPct = xPct;
        this.yPct = yPct;
        this.wPct = wPct;
        this.hPct = hPct;
        this.status = status;
    }

    public void updateStatus(SpotStatus newStatus) {
        this.status = newStatus;
    }
}
