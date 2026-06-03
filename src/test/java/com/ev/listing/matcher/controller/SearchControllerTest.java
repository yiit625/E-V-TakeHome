package com.ev.listing.matcher.controller;

import com.ev.listing.matcher.dto.SearchRequest;
import com.ev.listing.matcher.dto.SearchResultItem;
import com.ev.listing.matcher.service.SearchService;
import com.ev.listing.matcher.service.SearchServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SearchService searchService;

    @Test
    void search_validRequest_returns200WithSortedResults() throws Exception {
        List<SearchResultItem> mockResults = List.of(
                new SearchResultItem(UUID.randomUUID(), "Penthouse Hamburg",
                        BigDecimal.valueOf(1_500_000), "20095", 5, 200.0, 1.0),
                new SearchResultItem(UUID.randomUUID(), "Villa Munich",
                        BigDecimal.valueOf(1_800_000), "80331", 3, 100.0, 0.55)
        );

        when(searchService.searchListings(any())).thenReturn(mockResults);

        SearchRequest request = new SearchRequest();
        request.setTargetPrice(BigDecimal.valueOf(1_500_000));
        request.setMinRooms(4);
        request.setMinSquareMeters(120.0);

        List<SearchResultItem> results = performSearch(request);

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(SearchResultItem::getScore)
                .isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(results)
                .extracting(SearchResultItem::getTitle)
                .containsExactly("Penthouse Hamburg", "Villa Munich");
    }

    @Test
    void search_emptyRequest_returns200WithResults() throws Exception {
        when(searchService.searchListings(any())).thenReturn(List.of());

        List<SearchResultItem> results = performSearch(new SearchRequest());

        assertThat(results).isEmpty();
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
}