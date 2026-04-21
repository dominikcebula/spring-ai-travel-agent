package com.dominikcebula.spring.ai.products.api.products;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/v1/products")
public interface ProductsApi {

    @GetExchange
    List<Product> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search);

    @GetExchange("/{id}")
    Product getProductById(@PathVariable Long id);
}
