package com.dominikcebula.spring.ai.orders.orders;

import com.dominikcebula.spring.ai.orders.api.orders.Order;
import com.dominikcebula.spring.ai.orders.api.orders.OrderItem;
import com.dominikcebula.spring.ai.orders.api.orders.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderFactory {
    private final Order order;

    public OrderFactory(Order order) {
        this.order = order;
    }

    public Order withStatus(OrderStatus newStatus) {
        return new Order(
                order.orderId(), order.orderNumber(), order.orderDate(),
                order.customerName(), order.customerEmail(),
                newStatus, order.totalValue(), order.items(),
                order.createdAt(), LocalDateTime.now()
        );
    }

    public Order withUpdatedDetails(String newCustomerName, String newCustomerEmail,
                                    List<OrderItem> newItems, BigDecimal newTotalValue) {
        return new Order(
                order.orderId(), order.orderNumber(), order.orderDate(),
                newCustomerName, newCustomerEmail,
                OrderStatus.UPDATED, newTotalValue, newItems,
                order.createdAt(), LocalDateTime.now()
        );
    }
}
