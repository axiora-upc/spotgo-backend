package com.axiora.spotgo.monitoring.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "occupancyByHour")
@Getter
public class OccupancyByHour extends AbstractAggregateRoot<OccupancyByHour> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parkingId", nullable = false)
    private Long parkingId;

    @Column(nullable = false)
    private String hour;

    @Column(nullable = false)
    private Integer intensity;

    public OccupancyByHour() {
    }

    public OccupancyByHour(Long parkingId, String hour, Integer intensity) {
        this.parkingId = parkingId;
        this.hour = hour;
        this.intensity = intensity;
    }
}
