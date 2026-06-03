package com.ev.listing.matcher.service;

import com.ev.listing.matcher.dto.SearchRequest;
import com.ev.listing.matcher.dto.SearchResultItem;
import com.ev.listing.matcher.entities.PropertyListingEntity;
import com.ev.listing.matcher.enums.ListingStatus;
import com.ev.listing.matcher.repositories.PropertyListingRepository;
import com.ev.listing.matcher.scoring.ScoringEngine;
import com.ev.listing.matcher.service.impl.SearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {
    @Mock
    private PropertyListingRepository listingRepository;

    @Mock
    private ScoringEngine scoringEngine;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchListings_returnsResultsSortedByScoreDescending() {
        PropertyListingEntity listing1 = buildListing("Penthouse Hamburg", 1_500_000);
        PropertyListingEntity listing2 = buildListing("Villa Munich", 1_800_000);
        PropertyListingEntity listing3 = buildListing("Apartment Berlin", 900_000);

        when(listingRepository.findAllByStatus(ListingStatus.ACTIVE))
                .thenReturn(List.of(listing1, listing2, listing3));

        SearchRequest request = buildRequest();

        when(scoringEngine.score(eq(listing1), any())).thenReturn(buildResult("Penthouse Hamburg", 1.0));
        when(scoringEngine.score(eq(listing2), any())).thenReturn(buildResult("Villa Munich", 0.6));
        when(scoringEngine.score(eq(listing3), any())).thenReturn(buildResult("Apartment Berlin", 0.8));

        List<SearchResultItem> results = searchService.searchListings(request);

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getTitle()).isEqualTo("Penthouse Hamburg");
        assertThat(results.get(0).getScore()).isEqualTo(1.0);
        assertThat(results.get(1).getTitle()).isEqualTo("Apartment Berlin");
        assertThat(results.get(1).getScore()).isEqualTo(0.8);
        assertThat(results.get(2).getTitle()).isEqualTo("Villa Munich");
        assertThat(results.get(2).getScore()).isEqualTo(0.6);
    }

    @Test
    void searchListings_noActiveListings_returnsEmptyList() {
        when(listingRepository.findAllByStatus(ListingStatus.ACTIVE)).thenReturn(List.of());

        List<SearchResultItem> results = searchService.searchListings(buildRequest());

        assertThat(results).isEmpty();
        verify(scoringEngine, never()).score(any(), any());
    }

    @Test
    void searchListings_onlyFetchesActiveListings() {
        when(listingRepository.findAllByStatus(ListingStatus.ACTIVE)).thenReturn(List.of());

        searchService.searchListings(buildRequest());

        verify(listingRepository, times(1)).findAllByStatus(ListingStatus.ACTIVE);
        verifyNoMoreInteractions(listingRepository);
    }

    private SearchResultItem buildResult(String title, double score) {
        return new SearchResultItem(UUID.randomUUID(), title,
                BigDecimal.valueOf(1_500_000), "20095", 4, 120.0, score);
    }

    private SearchRequest buildRequest() {
        SearchRequest request = new SearchRequest();
        request.setTargetPrice(BigDecimal.valueOf(1_500_000));
        request.setMinRooms(4);
        request.setMinSquareMeters(120.0);
        return request;
    }

    private PropertyListingEntity buildListing(String title, double price) {
        PropertyListingEntity listing = new PropertyListingEntity();
        listing.setId(UUID.randomUUID());
        listing.setTitle(title);
        listing.setPrice(BigDecimal.valueOf(price));
        listing.setStatus(ListingStatus.ACTIVE);
        return listing;
    }
}
