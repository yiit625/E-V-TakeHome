package com.ev.listing.matcher.seeder;

import com.ev.listing.matcher.entities.AgentEntity;
import com.ev.listing.matcher.repositories.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AgentRepository agentRepository;

    @Override
    public void run(String... args) {
        if (agentRepository.count() > 0) {
            log.info("Seed data already present, skipping...");
            return;
        }

        AgentEntity anna = new AgentEntity();
        anna.setName("Anna Müller");
        anna.setEmail("anna.mueller@engelvoelkers.com");
        anna.setTargetZipCode("20095");

        AgentEntity ben = new AgentEntity();
        ben.setName("Ben Schmidt");
        ben.setEmail("ben.schmidt@engelvoelkers.com");
        ben.setTargetZipCode("10115");

        AgentEntity clara = new AgentEntity();
        clara.setName("Clara Weber");
        clara.setEmail("clara.weber@engelvoelkers.com");
        clara.setTargetZipCode("80331");

        agentRepository.saveAll(List.of(anna, ben, clara));
        log.info("Seeded 3 agents successfully.");
    }
}
