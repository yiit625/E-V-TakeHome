package com.ev.listing.matcher.scoring;

import com.ev.listing.matcher.configuration.ScoringWeightsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoringEngine {
    private final ScoringWeightsConfig weights;

    public double score(BigDecimal propertyPrice, Integer propertyRooms, Double propertySquareMeters,
                        BigDecimal targetPrice, Integer minRooms, Double minSquareMeters) {
        double priceScore = calculatePriceScore(propertyPrice, targetPrice);
        double roomsScore = calculateRoomsScore(propertyRooms, minRooms);
        double spaceScore = calculateSpaceScore(propertySquareMeters, minSquareMeters);
        double totalWeight = weights.getPrice() + weights.getRooms() + weights.getSpace();

        log.debug("Score: price={}, rooms={}, space={}, totalWeight={}", priceScore, roomsScore, spaceScore, totalWeight);

        return (priceScore * weights.getPrice()
              + roomsScore * weights.getRooms()
              + spaceScore * weights.getSpace())
              / totalWeight;
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