package com.ev.listing.matcher.entities;

import com.ev.listing.matcher.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PropertyListingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private Integer numberOfRooms;

    @Column(nullable = false)
    private Double squareMeters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status;

    private UUID assignedAgentId;
}
