package com.ev.listing.matcher.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SearchRequest {
    private BigDecimal targetPrice;
    private Integer minRooms;
    private Double minSquareMeters;
}
