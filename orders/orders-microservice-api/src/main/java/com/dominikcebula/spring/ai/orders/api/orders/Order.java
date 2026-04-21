package com.dominikcebula.spring.ai.orders.api.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID orderId,
        String orderNumber,
        LocalDateTime orderDate,
        String customerName,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalValue,
        List<OrderItem> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
