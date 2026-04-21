package com.dominikcebula.spring.ai.orders.api.orders;

import java.math.BigDecimal;

public record OrderItem(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
) {
}
