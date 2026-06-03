package com.ev.listing.matcher.integration;

import com.ev.listing.matcher.dto.SearchRequest;
import com.ev.listing.matcher.dto.SearchResultItem;
import com.ev.listing.matcher.entities.PropertyListingEntity;
import com.ev.listing.matcher.enums.ListingStatus;
import com.ev.listing.matcher.repositories.PropertyListingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PropertyListingRepository listingRepository;

    @BeforeEach
    void cleanUp() {
        listingRepository.deleteAll();
    }

    @Test
    void search_returnsOnlyActiveListings() throws Exception {
        listingRepository.saveAll(List.of(
                buildListing("Penthouse Hamburg", 1_500_000, 5, 200.0, ListingStatus.ACTIVE),
                buildListing("Hidden Villa", 1_500_000, 5, 200.0, ListingStatus.PREPARATION),
                buildListing("Sold Apartment", 1_500_000, 5, 200.0, ListingStatus.SOLD)
        ));

        List<SearchResultItem> results = performSearch(buildRequest());

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getTitle()).isEqualTo("Penthouse Hamburg");
        assertThat(results.getFirst().getScore()).isEqualTo(1.0);
    }

    @Test
    void search_multipleActiveListings_sortedByScoreDescending() throws Exception {
        listingRepository.saveAll(List.of(
                buildListing("Perfect Match", 1_500_000, 5, 200.0, ListingStatus.ACTIVE),
                buildListing("Over Budget", 3_000_000, 5, 200.0, ListingStatus.ACTIVE),
                buildListing("Small Rooms", 1_500_000, 2, 200.0, ListingStatus.ACTIVE)
        ));

        List<SearchResultItem> results = performSearch(buildRequest());

        assertThat(results).hasSize(3);
        assertThat(results)
                .extracting(SearchResultItem::getScore)
                .isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(results)
                .extracting(SearchResultItem::getTitle)
                .containsExactly("Perfect Match", "Small Rooms", "Over Budget");
    }

    @Test
    void search_noActiveListings_returnsEmptyList() throws Exception {
        List<SearchResultItem> results = performSearch(buildRequest());

        assertThat(results).isEmpty();
    }

    @Test
    void search_emptyRequest_returnsAllActiveListings() throws Exception {
        listingRepository.saveAll(List.of(
                buildListing("Penthouse Hamburg", 1_500_000, 5, 200.0, ListingStatus.ACTIVE),
                buildListing("Villa Munich", 1_800_000, 4, 150.0, ListingStatus.ACTIVE)
        ));

        List<SearchResultItem> results = performSearch(new SearchRequest());

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(SearchResultItem::getTitle)
                .containsExactlyInAnyOrder("Penthouse Hamburg", "Villa Munich");
    }

    private List<SearchResultItem> performSearch(SearchRequest request) throws Exception {
        String json = mockMvc.perform(post("/api/properties/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CollectionType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, SearchResultItem.class);
        return objectMapper.readValue(json, listType);
    }

    private PropertyListingEntity buildListing(String title, double price,
                                         int rooms, double sqm, ListingStatus status) {
        PropertyListingEntity listing = new PropertyListingEntity();
        listing.setTitle(title);
        listing.setDescription("Test description");
        listing.setPrice(BigDecimal.valueOf(price));
        listing.setZipCode("20095");
        listing.setNumberOfRooms(rooms);
        listing.setSquareMeters(sqm);
        listing.setStatus(status);
        return listing;
    }

    private SearchRequest buildRequest() {
        SearchRequest request = new SearchRequest();
        request.setTargetPrice(BigDecimal.valueOf(1_500_000));
        request.setMinRooms(4);
        request.setMinSquareMeters(120.0);
        return request;
    }
}