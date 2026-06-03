package com.ev.listing.matcher.service.impl;

import com.ev.listing.matcher.dto.CreateListingRequest;
import com.ev.listing.matcher.dto.CreateListingResponse;
import com.ev.listing.matcher.entities.PropertyListingEntity;
import com.ev.listing.matcher.enums.ListingStatus;
import com.ev.listing.matcher.repositories.AgentRepository;
import com.ev.listing.matcher.repositories.PropertyListingRepository;
import com.ev.listing.matcher.service.ListingService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final PropertyListingRepository listingRepository;
    private final AgentRepository agentRepository;

    @Transactional
    @Override
    public CreateListingResponse createListing(CreateListingRequest request) {
        PropertyListingEntity listing = mapToEntity(request);

        agentRepository.findByTargetZipCode(request.getZipCode())
                .ifPresentOrElse(
                        agent -> {
                            listing.setAssignedAgentId(agent.getId());
                            log.info("Agent {} assigned to listing '{}'", agent.getName(), listing.getTitle());
                        },
                        () -> log.info("No agent found for ZIP '{}', listing unassigned", request.getZipCode())
                );

        PropertyListingEntity saved = listingRepository.save(listing);
        return mapToResponse(saved);
    }

    private CreateListingResponse mapToResponse(PropertyListingEntity entity) {
        return new CreateListingResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getZipCode(),
                entity.getNumberOfRooms(),
                entity.getSquareMeters(),
                entity.getStatus(),
                entity.getAssignedAgentId()
        );
    }

    private PropertyListingEntity mapToEntity(CreateListingRequest request) {
        PropertyListingEntity listing = new PropertyListingEntity();
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setZipCode(request.getZipCode());
        listing.setNumberOfRooms(request.getNumberOfRooms());
        listing.setSquareMeters(request.getSquareMeters());
        listing.setStatus(ListingStatus.PREPARATION);
        return listing;
    }
}