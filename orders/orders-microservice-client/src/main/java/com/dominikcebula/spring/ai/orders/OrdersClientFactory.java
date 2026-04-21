package com.dominikcebula.spring.ai.orders;

import com.dominikcebula.spring.ai.orders.api.orders.OrdersApi;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class OrdersClientFactory {
    private OrdersClientFactory() {
    }

    public static OrdersApi newOrdersApiClient(String baseUrl) {
        return createClient(OrdersApi.class, baseUrl);
    }

    private static <S> S createClient(Class<S> serviceType, String baseUrl) {
        RestClient restClient = RestClient.create(baseUrl);
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(serviceType);
    }
}
