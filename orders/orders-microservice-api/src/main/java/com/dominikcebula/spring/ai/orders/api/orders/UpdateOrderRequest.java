package com.dominikcebula.spring.ai.orders.api.orders;

import java.util.List;

public record UpdateOrderRequest(
        String customerName,
        String customerEmail,
        List<OrderItem> items
) {
}
