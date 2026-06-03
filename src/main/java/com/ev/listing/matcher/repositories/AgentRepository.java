package com.ev.listing.matcher.repositories;

import com.ev.listing.matcher.entities.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AgentRepository extends JpaRepository<AgentEntity, UUID> {
    Optional<AgentEntity> findByTargetZipCode(String zipCode);
}
