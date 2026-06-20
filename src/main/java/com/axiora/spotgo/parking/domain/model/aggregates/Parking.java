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

    @Column(name = "adminId")
    private Long adminId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column
    private String city;

    @Column(name = "totalSpaces", nullable = false)
    private Integer totalSpaces;

    @Column(name = "availableSpaces")
    private Integer availableSpaces;

    @Column(name = "totalFloors")
    private Integer totalFloors;

    @Column
    private Double averageOccupancy;

    @Column
    private Double occupancyTrendPercent;

    @Column
    private String peakHour;

    @Column
    private Double totalRevenue;

    @Column
    private String systemStatus;

    @Column(nullable = true)
    private Double rating;

    @Column(name = "pricePerHour", nullable = true)
    private Double pricePerHour;

    @Column
    private Double revenueTrendPercent;

    @Column
    private Integer totalCapacity;

    @Column
    private Double efficiencyIndex;

    public Parking() {
    }

    public Parking(Long adminId, String name, String address, String city,
                   Integer totalSpaces, Integer availableSpaces, Integer totalFloors,
                   Double averageOccupancy, Double occupancyTrendPercent, String peakHour,
                   Double totalRevenue, String systemStatus, Double rating, Double pricePerHour,
                   Double revenueTrendPercent, Integer totalCapacity, Double efficiencyIndex) {
        this.adminId = adminId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.totalSpaces = totalSpaces;
        this.availableSpaces = availableSpaces;
        this.totalFloors = totalFloors;
        this.averageOccupancy = averageOccupancy;
        this.occupancyTrendPercent = occupancyTrendPercent;
        this.peakHour = peakHour;
        this.totalRevenue = totalRevenue;
        this.systemStatus = systemStatus;
        this.rating = rating;
        this.pricePerHour = pricePerHour;
        this.revenueTrendPercent = revenueTrendPercent;
        this.totalCapacity = totalCapacity;
        this.efficiencyIndex = efficiencyIndex;
    }

    public void updateRating(Double rating) {
        this.rating = rating;
    }
}
