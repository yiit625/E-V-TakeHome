package com.ev.listing.matcher.controller;

import com.ev.listing.matcher.dto.SearchRequest;
import com.ev.listing.matcher.dto.SearchResultItem;
import com.ev.listing.matcher.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Search", description = "Similarity-based property search for end customers")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/properties")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "Search active listings by similarity",
            description = "Scores and ranks all ACTIVE listings based on price, rooms, and space criteria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranked results returned successfully")
    })
    @PostMapping("/search")
    public ResponseEntity<List<SearchResultItem>> searchListings(@RequestBody SearchRequest request) {
        log.info("POST /api/properties/search - searching with criteria: {}", request);
        List<SearchResultItem> results = searchService.searchListings(request);
        return ResponseEntity.ok(results);
    }
}