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

    @Column(name = "totalSpots", nullable = false)
    private Integer totalSpots;

    @Column(nullable = true)
    private Double rating;

    @Column(name = "pricePerHour", nullable = true)
    private Double pricePerHour;

    public Parking() {
    }

    public Parking(String name, String location, Integer totalSpots, Double rating, Double pricePerHour) {
        this.name = name;
        this.location = location;
        this.totalSpots = totalSpots;
        this.rating = rating;
        this.pricePerHour = pricePerHour;
    }

    public void updateRating(Double rating) {
        this.rating = rating;
    }
}
