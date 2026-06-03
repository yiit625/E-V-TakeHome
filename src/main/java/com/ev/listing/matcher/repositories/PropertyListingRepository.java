package com.ev.listing.matcher.repositories;

import com.ev.listing.matcher.entities.PropertyListingEntity;
import com.ev.listing.matcher.enums.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyListingRepository extends JpaRepository<PropertyListingEntity, UUID> {
    List<PropertyListingEntity> findAllByStatus(ListingStatus status);
}
