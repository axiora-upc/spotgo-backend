package com.axiora.spotgo.parking.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Coordinates {
    private Double x;
    private Double y;

    public Coordinates() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public Coordinates(Double x, Double y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        this.x = x;
        this.y = y;
    }
}
