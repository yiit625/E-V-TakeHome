package com.ev.listing.matcher.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Engel & Völkers Listing & Matcher API")
                        .description("Backend service for managing property listings and intelligent property matching")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Engel & Völkers Engineering")
                                .email("engineering@engelvoelkers.com")));
    }
}