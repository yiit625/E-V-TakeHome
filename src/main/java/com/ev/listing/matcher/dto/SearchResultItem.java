package com.ev.listing.matcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SearchResultItem {
    private UUID id;
    private String title;
    private BigDecimal price;
    private String zipCode;
    private Integer numberOfRooms;
    private Double squareMeters;
    private double score;
}
