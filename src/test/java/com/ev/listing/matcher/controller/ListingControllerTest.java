package com.ev.listing.matcher.controller;

import com.ev.listing.matcher.dto.CreateListingRequest;
import com.ev.listing.matcher.dto.CreateListingResponse;
import com.ev.listing.matcher.enums.ListingStatus;
import com.ev.listing.matcher.service.ListingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ListingService listingService;

    @Test
    void createListing_validRequest_returns201() throws Exception {
        CreateListingRequest request = buildValidRequest();

        CreateListingResponse saved = new CreateListingResponse(
                UUID.randomUUID(), request.getTitle(), request.getDescription(),
                request.getPrice(), request.getZipCode(), request.getNumberOfRooms(),
                request.getSquareMeters(), ListingStatus.PREPARATION, null
        );

        when(listingService.createListing(any())).thenReturn(saved);

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        CreateListingResponse response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CreateListingResponse.class
        );

        assertThat(response.getTitle()).isEqualTo("Penthouse Hamburg");
        assertThat(response.getStatus()).isEqualTo(ListingStatus.PREPARATION);
        assertThat(response.getId()).isNotNull();
    }

    @Test
    void createListing_blankTitle_returns400() throws Exception {
        CreateListingRequest request = buildValidRequest();
        request.setTitle("");

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody).contains("Title cannot be blank");
    }

    @Test
    void createListing_negativePrive_returns400() throws Exception {
        CreateListingRequest request = buildValidRequest();
        request.setPrice(BigDecimal.valueOf(-1));

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody).contains("Price must be greater than zero");
    }

    @Test
    void createListing_blankZipCode_returns400() throws Exception {
        CreateListingRequest request = buildValidRequest();
        request.setZipCode("");

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody).contains("ZIP code cannot be blank");
    }

    @Test
    void createListing_blankDescription_returns400() throws Exception {
        CreateListingRequest request = buildValidRequest();
        request.setDescription("");

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody).contains("Description cannot be blank");
    }

    @Test
    void createListing_negativeSpaceAndRooms_returns400() throws Exception {
        CreateListingRequest request = buildValidRequest();
        request.setSquareMeters(0.0);
        request.setNumberOfRooms(-1);

        MvcResult mvcResult = mockMvc.perform(post("/api/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody).contains("Square meters must be greater than zero");
        assertThat(responseBody).contains("Number of rooms must be greater than zero");
    }

    private CreateListingRequest buildValidRequest() {
        CreateListingRequest request = new CreateListingRequest();
        request.setTitle("Penthouse Hamburg");
        request.setDescription("Luxury penthouse in the heart of Hamburg");
        request.setPrice(BigDecimal.valueOf(1_500_000));
        request.setZipCode("20095");
        request.setNumberOfRooms(5);
        request.setSquareMeters(200.0);
        return request;
    }
}