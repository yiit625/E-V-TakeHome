package com.ev.listing.matcher.scoring;

import com.ev.listing.matcher.dto.SearchRequest;
import com.ev.listing.matcher.dto.SearchResultItem;
import com.ev.listing.matcher.entities.PropertyListingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ScoringEngineTest {
    private ScoringEngine scoringEngine;

    @BeforeEach
    void setUp() {
        scoringEngine = new ScoringEngine();
    }

    @Test
    void score_perfectMatch_returns1() {
        PropertyListingEntity listing = buildListing(1_500_000, 4, 120.0);
        SearchRequest request = buildRequest(1_500_000, 4, 120.0);

        SearchResultItem result = scoringEngine.score(listing, request);

        assertThat(result.getScore()).isCloseTo(1.0, within(0.001));
    }

    @Test
    void score_priceFarOver_priceScoreIsZero() {
        // price is 3x the target → price score should be 0
        PropertyListingEntity listing = buildListing(4_500_000, 4, 120.0);
        SearchRequest request = buildRequest(1_500_000, 4, 120.0);

        SearchResultItem result = scoringEngine.score(listing, request);

        // priceScore=0, roomsScore=1, spaceScore=1 → total = 0.666
        assertThat(result.getScore()).isCloseTo(0.666, within(0.001));
    }

    @Test
    void score_roomsBelowMinimum_reducesScore() {
        // 2 rooms vs min 4 → roomsScore = 0.5
        PropertyListingEntity listing = buildListing(1_500_000, 2, 120.0);
        SearchRequest request = buildRequest(1_500_000, 4, 120.0);

        SearchResultItem result = scoringEngine.score(listing, request);

        // priceScore=1, roomsScore=0.5, spaceScore=1 → total = 0.833
        assertThat(result.getScore()).isCloseTo(0.833, within(0.001));
    }

    @Test
    void score_spaceBelowMinimum_reducesScore() {
        // 60sqm vs min 120 → spaceScore = 0.5
        PropertyListingEntity listing = buildListing(1_500_000, 4, 60.0);
        SearchRequest request = buildRequest(1_500_000, 4, 120.0);

        SearchResultItem result = scoringEngine.score(listing, request);

        // priceScore=1, roomsScore=1, spaceScore=0.5 → total = 0.833
        assertThat(result.getScore()).isCloseTo(0.833, within(0.001));
    }

    @Test
    void score_nullCriteria_defaultsToFullScore() {
        PropertyListingEntity listing = buildListing(1_500_000, 4, 120.0);
        SearchRequest request = new SearchRequest(); // all nulls

        SearchResultItem result = scoringEngine.score(listing, request);

        assertThat(result.getScore()).isCloseTo(1.0, within(0.001));
    }

    private PropertyListingEntity buildListing(double price, int rooms, double sqm) {
        PropertyListingEntity listing = new PropertyListingEntity();
        listing.setTitle("Test Listing");
        listing.setPrice(BigDecimal.valueOf(price));
        listing.setNumberOfRooms(rooms);
        listing.setSquareMeters(sqm);
        return listing;
    }

    private SearchRequest buildRequest(double targetPrice, int minRooms, double minSqm) {
        SearchRequest request = new SearchRequest();
        request.setTargetPrice(BigDecimal.valueOf(targetPrice));
        request.setMinRooms(minRooms);
        request.setMinSquareMeters(minSqm);
        return request;
    }
}
