package com.ev.listing.matcher.dto;

import com.ev.listing.matcher.enums.ListingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateListingResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private String zipCode;
    private Integer numberOfRooms;
    private Double squareMeters;
    private ListingStatus status;
    private UUID assignedAgentId;
}
