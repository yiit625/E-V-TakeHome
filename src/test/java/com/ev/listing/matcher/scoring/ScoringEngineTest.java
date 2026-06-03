package com.ev.listing.matcher.scoring;

import com.ev.listing.matcher.configuration.ScoringWeightsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class ScoringEngineTest {
    private ScoringEngine scoringEngine;

    @BeforeEach
    void setUp() {
        ScoringWeightsConfig weights = new ScoringWeightsConfig();
        weights.setPrice(1.0);
        weights.setRooms(1.0);
        weights.setSpace(1.0);
        scoringEngine = new ScoringEngine(weights);
    }

    @Test
    void score_perfectMatch_returns1() {
        double result = scoringEngine.score(
                BigDecimal.valueOf(1_500_000), 4, 120.0,
                BigDecimal.valueOf(1_500_000), 4, 120.0
        );

        assertThat(result).isCloseTo(1.0, within(0.001));
    }

    @Test
    void score_priceFarOver_priceScoreIsZero() {
        // price is 3x the target → price score should be 0
        double result = scoringEngine.score(
                BigDecimal.valueOf(4_500_000), 4, 120.0,
                BigDecimal.valueOf(1_500_000), 4, 120.0
        );

        // priceScore=0, roomsScore=1, spaceScore=1 → total = 0.666
        assertThat(result).isCloseTo(0.666, within(0.001));
    }

    @Test
    void score_roomsBelowMinimum_reducesScore() {
        // 2 rooms vs min 4 → roomsScore = 0.5
        double result = scoringEngine.score(
                BigDecimal.valueOf(1_500_000), 2, 120.0,
                BigDecimal.valueOf(1_500_000), 4, 120.0
        );

        // priceScore=1, roomsScore=0.5, spaceScore=1 → total = 0.833
        assertThat(result).isCloseTo(0.833, within(0.001));
    }

    @Test
    void score_spaceBelowMinimum_reducesScore() {
        // 60sqm vs min 120 → spaceScore = 0.5
        double result = scoringEngine.score(
                BigDecimal.valueOf(1_500_000), 4, 60.0,
                BigDecimal.valueOf(1_500_000), 4, 120.0
        );

        // priceScore=1, roomsScore=1, spaceScore=0.5 → total = 0.833
        assertThat(result).isCloseTo(0.833, within(0.001));
    }

    @Test
    void score_nullCriteria_defaultsToFullScore() {
        double result = scoringEngine.score(
                BigDecimal.valueOf(1_500_000), 4, 120.0,
                null, null, null
        );

        assertThat(result).isCloseTo(1.0, within(0.001));
    }
}
