package com.ev.listing.matcher.service.impl;

import com.ev.listing.matcher.dto.SearchRequest;
import com.ev.listing.matcher.dto.SearchResultItem;
import com.ev.listing.matcher.enums.ListingStatus;
import com.ev.listing.matcher.repositories.PropertyListingRepository;
import com.ev.listing.matcher.scoring.ScoringEngine;
import com.ev.listing.matcher.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final PropertyListingRepository listingRepository;
    private final ScoringEngine scoringEngine;

    @Override
    public List<SearchResultItem> searchListings(SearchRequest request) {
        return listingRepository.findAllByStatus(ListingStatus.ACTIVE)
                .stream()
                .map(listing -> {
                    double score = scoringEngine.score(
                            listing.getPrice(), listing.getNumberOfRooms(), listing.getSquareMeters(),
                            request.getTargetPrice(), request.getMinRooms(), request.getMinSquareMeters()
                    );
                    return new SearchResultItem(
                            listing.getId(), listing.getTitle(), listing.getPrice(),
                            listing.getZipCode(), listing.getNumberOfRooms(), listing.getSquareMeters(),
                            score
                    );
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .toList();
    }
}