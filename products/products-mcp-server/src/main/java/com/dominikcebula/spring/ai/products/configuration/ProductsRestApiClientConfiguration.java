package com.dominikcebula.spring.ai.products.configuration;

import com.dominikcebula.spring.ai.products.ProductsClientFactory;
import com.dominikcebula.spring.ai.products.api.products.ProductsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductsRestApiClientConfiguration {
    @Value("${products.api.base-uri}")
    private String baseUri;

    @Bean
    public ProductsApi productsApi() {
        return ProductsClientFactory.newProductsApiClient(baseUri);
    }
}
