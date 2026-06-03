package com.ev.listing.matcher.scoring;

import com.ev.listing.matcher.dto.SearchRequest;
import com.ev.listing.matcher.dto.SearchResultItem;
import com.ev.listing.matcher.entities.PropertyListingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ScoringEngine {
    public SearchResultItem score(PropertyListingEntity listing, SearchRequest request) {
        double priceScore = calculatePriceScore(listing.getPrice(), request.getTargetPrice());
        double roomsScore = calculateRoomsScore(listing.getNumberOfRooms(), request.getMinRooms());
        double spaceScore = calculateSpaceScore(listing.getSquareMeters(), request.getMinSquareMeters());
        double total = (priceScore + roomsScore + spaceScore) / 3.0;

        log.debug("Score for '{}': price={}, rooms={}, space={}, total={}",
                listing.getTitle(), priceScore, roomsScore, spaceScore, total);

        return new SearchResultItem(
                listing.getId(),
                listing.getTitle(),
                listing.getPrice(),
                listing.getZipCode(),
                listing.getNumberOfRooms(),
                listing.getSquareMeters(),
                total
        );
    }

    private double calculatePriceScore(BigDecimal propertyPrice, BigDecimal targetPrice) {
        if (targetPrice == null || targetPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 1.0;
        }
        double variance = Math.abs(propertyPrice.subtract(targetPrice).doubleValue());
        double score = 1.0 - (variance / targetPrice.doubleValue());
        return Math.max(0.0, score);
    }

    private double calculateRoomsScore(Integer propertyRooms, Integer minRooms) {
        if (minRooms == null || minRooms == 0) {
            return 1.0;
        }
        return propertyRooms >= minRooms ? 1.0 : (double) propertyRooms / minRooms;
    }

    private double calculateSpaceScore(Double propertySquareMeters, Double minSquareMeters) {
        if (minSquareMeters == null || minSquareMeters == 0) {
            return 1.0;
        }
        return propertySquareMeters >= minSquareMeters ? 1.0 : propertySquareMeters / minSquareMeters;
    }
}