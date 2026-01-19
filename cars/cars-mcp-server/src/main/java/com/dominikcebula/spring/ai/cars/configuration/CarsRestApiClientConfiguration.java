package com.dominikcebula.spring.ai.cars.configuration;

import com.dominikcebula.spring.ai.cars.CarsClientFactory;
import com.dominikcebula.spring.ai.cars.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.cars.api.cars.CarsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarsRestApiClientConfiguration {
    @Value("${cars.api.base-uri}")
    private String baseUri;

    @Bean
    public BookingsApi bookingsApi() {
        return CarsClientFactory.newBookingsApiClient(baseUri);
    }

    @Bean
    public CarsApi carsApi() {
        return CarsClientFactory.newCarsApiClient(baseUri);
    }
}
