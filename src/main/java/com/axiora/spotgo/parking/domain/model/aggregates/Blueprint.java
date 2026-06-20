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

    @Column(name = "adminId")
    private Long adminId;

    @Column(name = "parkingId", nullable = false)
    private Long parkingId;

    @Column(name = "name")
    private String name;

    @Column(name = "dataUrl", columnDefinition = "TEXT")
    private String dataUrl;

    public Blueprint() {
    }

    public Blueprint(Long adminId, Long parkingId, String name, String dataUrl) {
        this.adminId = adminId;
        this.parkingId = parkingId;
        this.name = name;
        this.dataUrl = dataUrl;
    }
}
