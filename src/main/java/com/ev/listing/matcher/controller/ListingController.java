package com.ev.listing.matcher.controller;

import com.ev.listing.matcher.dto.CreateListingRequest;
import com.ev.listing.matcher.dto.CreateListingResponse;
import com.ev.listing.matcher.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Listings", description = "Property listing management")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    @Operation(summary = "Create a new property listing",
            description = "Registers a new listing and auto-assigns an agent if ZIP code matches")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Listing created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<CreateListingResponse> createListing(@Valid @RequestBody CreateListingRequest request) {
        log.info("POST /api/listings - creating listing '{}'", request.getTitle());
        CreateListingResponse created = listingService.createListing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}