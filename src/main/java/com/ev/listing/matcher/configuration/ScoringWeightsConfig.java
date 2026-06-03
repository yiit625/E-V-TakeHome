package com.ev.listing.matcher.configuration;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "scoring.weights")
public class ScoringWeightsConfig {

    @PositiveOrZero(message = "Price weight must be zero or positive")
    private double price = 1.0;

    @PositiveOrZero(message = "Rooms weight must be zero or positive")
    private double rooms = 1.0;

    @PositiveOrZero(message = "Space weight must be zero or positive")
    private double space = 1.0;

    @AssertTrue(message = "At least one scoring weight must be greater than zero")
    public boolean isAtLeastOneWeightPositive() {
        return price + rooms + space > 0;
    }
}
