package com.axiora.spotgo.monitoring.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "weeklyTrends")
@Getter
public class WeeklyTrend extends AbstractAggregateRoot<WeeklyTrend> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parkingId", nullable = false)
    private Long parkingId;

    @Column(nullable = false)
    private String day;

    @Column(nullable = false)
    private Double value;

    public WeeklyTrend() {
    }

    public WeeklyTrend(Long parkingId, String day, Double value) {
        this.parkingId = parkingId;
        this.day = day;
        this.value = value;
    }
}
