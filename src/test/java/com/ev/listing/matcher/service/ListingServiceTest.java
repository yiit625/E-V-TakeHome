package com.ev.listing.matcher.service;

import com.ev.listing.matcher.dto.CreateListingRequest;
import com.ev.listing.matcher.dto.CreateListingResponse;
import com.ev.listing.matcher.entities.AgentEntity;
import com.ev.listing.matcher.entities.PropertyListingEntity;
import com.ev.listing.matcher.enums.ListingStatus;
import com.ev.listing.matcher.repositories.AgentRepository;
import com.ev.listing.matcher.repositories.PropertyListingRepository;
import com.ev.listing.matcher.service.impl.ListingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListingServiceTest {
    @Mock
    private PropertyListingRepository listingRepository;

    @Mock
    private AgentRepository agentRepository;

    @InjectMocks
    private ListingServiceImpl listingService;

    @Test
    void createListing_withMatchingZipCode_assignsAgent() {
        AgentEntity agent = new AgentEntity();
        agent.setId(UUID.randomUUID());
        agent.setName("Anna Müller");
        agent.setTargetZipCode("20095");

        when(agentRepository.findByTargetZipCode("20095")).thenReturn(Optional.of(agent));
        when(listingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateListingResponse result = listingService.createListing(buildRequest("20095"));

        assertThat(result.getAssignedAgentId()).isEqualTo(agent.getId());
        assertThat(result.getStatus()).isEqualTo(ListingStatus.PREPARATION);
    }

    @Test
    void createListing_withNoMatchingZipCode_leavesAgentNull() {
        when(agentRepository.findByTargetZipCode("99999")).thenReturn(Optional.empty());
        when(listingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateListingResponse result = listingService.createListing(buildRequest("99999"));

        assertThat(result.getAssignedAgentId()).isNull();
        assertThat(result.getStatus()).isEqualTo(ListingStatus.PREPARATION);
    }

    @Test
    void createListing_alwaysSetsStatusToPreparation() {
        when(agentRepository.findByTargetZipCode(any())).thenReturn(Optional.empty());
        when(listingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateListingResponse result = listingService.createListing(buildRequest("20095"));

        assertThat(result.getStatus()).isEqualTo(ListingStatus.PREPARATION);
    }

    private CreateListingRequest buildRequest(String zipCode) {
        CreateListingRequest request = new CreateListingRequest();
        request.setTitle("Penthouse Hamburg");
        request.setDescription("Luxury penthouse");
        request.setPrice(BigDecimal.valueOf(1_500_000));
        request.setZipCode(zipCode);
        request.setNumberOfRooms(5);
        request.setSquareMeters(200.0);
        return request;
    }
}
