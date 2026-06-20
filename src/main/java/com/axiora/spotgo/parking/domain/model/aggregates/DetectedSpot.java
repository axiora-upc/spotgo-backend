package com.axiora.spotgo.parking.domain.model.aggregates;

import com.axiora.spotgo.parking.domain.model.valueobjects.SpotStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "detectedSpots")
@Getter
public class DetectedSpot extends AbstractAggregateRoot<DetectedSpot> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "localId")
    private Integer localId;

    @Column(name = "blueprintId", nullable = false)
    private Long blueprintId;

    @Column(name = "parkingId")
    private Long parkingId;

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

    public DetectedSpot(Integer localId, Long blueprintId, Long parkingId, Integer row, Integer col,
                        Double xPct, Double yPct, Double wPct, Double hPct, SpotStatus status) {
        this.localId = localId;
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
