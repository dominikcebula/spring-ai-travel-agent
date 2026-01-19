package com.dominikcebula.spring.ai.flights.configuration;

import com.dominikcebula.spring.ai.flights.FlightsClientFactory;
import com.dominikcebula.spring.ai.flights.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.flights.api.flights.FlightsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlightsRestApiClientConfiguration {
    @Value("${flights.api.base-uri}")
    private String baseUri;

    @Bean
    public BookingsApi bookingsApi() {
        return FlightsClientFactory.newBookingsApiClient(baseUri);
    }

    @Bean
    public FlightsApi flightsApi() {
        return FlightsClientFactory.newFlightsApiClient(baseUri);
    }
}
