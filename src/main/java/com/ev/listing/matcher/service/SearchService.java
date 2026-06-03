package com.ev.listing.matcher.service;

import com.ev.listing.matcher.dto.SearchRequest;
import com.ev.listing.matcher.dto.SearchResultItem;

import java.util.List;

public interface SearchService {
    List<SearchResultItem> searchListings(SearchRequest request);
}
