package com.ev.listing.matcher.integration;

import com.ev.listing.matcher.dto.CreateListingRequest;
import com.ev.listing.matcher.dto.CreateListingResponse;
import com.ev.listing.matcher.entities.PropertyListingEntity;
import com.ev.listing.matcher.enums.ListingStatus;
import com.ev.listing.matcher.repositories.PropertyListingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ListingIntegrationTest {

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
    void createListing_withMatchingZipCode_agentIsAssigned() throws Exception {
        // 20095 matches Anna Müller from DataSeeder
        CreateListingRequest request = buildRequest("20095");

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CreateListingResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CreateListingResponse.class
        );

        // assert HTTP response
        assertThat(response.getAssignedAgentId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(ListingStatus.PREPARATION);

        // assert database state
        List<PropertyListingEntity> all = listingRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.getFirst().getAssignedAgentId()).isNotNull();
        assertThat(all.getFirst().getStatus()).isEqualTo(ListingStatus.PREPARATION);
    }

    @Test
    void createListing_withNoMatchingZipCode_agentIsNull() throws Exception {
        CreateListingRequest request = buildRequest("99999");

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CreateListingResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CreateListingResponse.class
        );

        // assert HTTP response
        assertThat(response.getAssignedAgentId()).isNull();

        // assert database state
        List<PropertyListingEntity> all = listingRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.getFirst().getAssignedAgentId()).isNull();
    }

    @Test
    void createListing_invalidRequest_returns400WithValidationErrors() throws Exception {
        CreateListingRequest request = buildRequest("20095");
        request.setTitle("");
        request.setPrice(BigDecimal.valueOf(-1));

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        // assert validation messages
        assertThat(responseBody).contains("Title cannot be blank");
        assertThat(responseBody).contains("Price must be greater than zero");

        // assert nothing was persisted
        assertThat(listingRepository.findAll()).isEmpty();
    }

    private CreateListingRequest buildRequest(String zipCode) {
        CreateListingRequest request = new CreateListingRequest();
        request.setTitle("Penthouse Hamburg");
        request.setDescription("Luxury penthouse in the heart of Hamburg");
        request.setPrice(BigDecimal.valueOf(1_500_000));
        request.setZipCode(zipCode);
        request.setNumberOfRooms(5);
        request.setSquareMeters(200.0);
        return request;
    }
}