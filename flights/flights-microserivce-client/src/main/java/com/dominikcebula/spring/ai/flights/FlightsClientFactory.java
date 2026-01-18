package com.dominikcebula.spring.ai.flights;

import com.dominikcebula.spring.ai.flights.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.flights.api.flights.FlightsApi;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class FlightsClientFactory {
    private FlightsClientFactory() {
    }

    public static BookingsApi newBookingsApiClient(String baseUrl) {
        return createClient(BookingsApi.class, baseUrl);
    }

    public static FlightsApi newFlightsApiClient(String baseUrl) {
        return createClient(FlightsApi.class, baseUrl);
    }

    private static <S> S createClient(Class<S> serviceType, String baseUrl) {
        RestClient restClient = RestClient.create(baseUrl);
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(serviceType);
    }
}
