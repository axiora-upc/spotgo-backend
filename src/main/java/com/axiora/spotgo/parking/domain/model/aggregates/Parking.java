package com.axiora.spotgo.parking.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "parkings")
@Getter
public class Parking extends AbstractAggregateRoot<Parking> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "total_spots", nullable = false)
    private Integer totalSpots;

    public Parking() {
    }

    public Parking(String name, String location, Integer totalSpots) {
        this.name = name;
        this.location = location;
        this.totalSpots = totalSpots;
    }
}
