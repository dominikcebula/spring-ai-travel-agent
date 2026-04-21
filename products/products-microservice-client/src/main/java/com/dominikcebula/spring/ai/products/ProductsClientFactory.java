package com.dominikcebula.spring.ai.products;

import com.dominikcebula.spring.ai.products.api.products.ProductsApi;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class ProductsClientFactory {
    private ProductsClientFactory() {
    }

    public static ProductsApi newProductsApiClient(String baseUrl) {
        return createClient(ProductsApi.class, baseUrl);
    }

    private static <S> S createClient(Class<S> serviceType, String baseUrl) {
        RestClient restClient = RestClient.create(baseUrl);
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(serviceType);
    }
}
