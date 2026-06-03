package com.ev.listing.matcher.service;

import com.ev.listing.matcher.dto.CreateListingRequest;
import com.ev.listing.matcher.dto.CreateListingResponse;

public interface ListingService {
    CreateListingResponse createListing(CreateListingRequest request);
}