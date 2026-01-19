package com.dominikcebula.spring.ai.hotels.configuration;

import com.dominikcebula.spring.ai.hotels.HotelsClientFactory;
import com.dominikcebula.spring.ai.hotels.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.hotels.api.rooms.HotelsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HotelsRestApiClientConfiguration {
    @Value("${hotels.api.base-uri}")
    private String baseUri;

    @Bean
    public BookingsApi bookingsApi() {
        return HotelsClientFactory.newBookingsApiClient(baseUri);
    }

    @Bean
    public HotelsApi hotelsApi() {
        return HotelsClientFactory.newHotelsApiClient(baseUri);
    }
}
