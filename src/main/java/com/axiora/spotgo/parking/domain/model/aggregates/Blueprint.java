package com.axiora.spotgo.parking.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

@Entity
@Table(name = "blueprints")
@Getter
public class Blueprint extends AbstractAggregateRoot<Blueprint> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imageUrl", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "parkingId", nullable = false)
    private Long parkingId;

    public Blueprint() {
    }

    public Blueprint(String imageUrl, Long parkingId) {
        this.imageUrl = imageUrl;
        this.parkingId = parkingId;
    }
}
