package com.axiora.spotgo.parking.domain.model.aggregates;

import com.axiora.spotgo.parking.domain.model.valueobjects.Coordinates;
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

    @Embedded
    private Coordinates coordinates;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotStatus status;

    @Column(name = "blueprintId", nullable = false)
    private Long blueprintId;

    public DetectedSpot() {
    }

    public DetectedSpot(Coordinates coordinates, Long blueprintId) {
        this.coordinates = coordinates;
        this.blueprintId = blueprintId;
        this.status = SpotStatus.FREE;
    }

    public void updateStatus(SpotStatus newStatus) {
        this.status = newStatus;
    }
}
