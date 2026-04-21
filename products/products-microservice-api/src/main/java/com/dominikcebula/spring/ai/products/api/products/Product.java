package com.dominikcebula.spring.ai.products.api.products;

import java.math.BigDecimal;
import java.util.List;

public record Product(
        Long id,
        String name,
        BigDecimal price,
        String category,
        int stock,
        String sku,
        double rating,
        int popularity,
        List<String> tags,
        String warehouseCountry
) {
}
