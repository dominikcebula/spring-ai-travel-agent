package com.dominikcebula.spring.ai.orders.configuration;

import com.dominikcebula.spring.ai.orders.OrdersClientFactory;
import com.dominikcebula.spring.ai.orders.api.orders.OrdersApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdersRestApiClientConfiguration {
    @Value("${orders.api.base-uri}")
    private String baseUri;

    @Bean
    public OrdersApi ordersApi() {
        return OrdersClientFactory.newOrdersApiClient(baseUri);
    }
}
