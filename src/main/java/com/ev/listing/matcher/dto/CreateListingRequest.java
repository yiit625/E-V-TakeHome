package com.ev.listing.matcher.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateListingRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @NotBlank(message = "ZIP code cannot be blank")
    private String zipCode;

    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "Number of rooms must be greater than zero")
    private Integer numberOfRooms;

    @NotNull(message = "Square meters is required")
    @Positive(message = "Square meters must be greater than zero")
    private Double squareMeters;
}
