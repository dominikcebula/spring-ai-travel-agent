package com.dominikcebula.spring.ai.hotels;

import com.dominikcebula.spring.ai.hotels.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.hotels.api.rooms.HotelsApi;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class HotelsClientFactory {
    private HotelsClientFactory() {
    }

    public static BookingsApi newBookingsApiClient(String baseUrl) {
        return createClient(BookingsApi.class, baseUrl);
    }

    public static HotelsApi newHotelsApiClient(String baseUrl) {
        return createClient(HotelsApi.class, baseUrl);
    }

    private static <S> S createClient(Class<S> serviceType, String baseUrl) {
        RestClient restClient = RestClient.create(baseUrl);
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(serviceType);
    }
}
