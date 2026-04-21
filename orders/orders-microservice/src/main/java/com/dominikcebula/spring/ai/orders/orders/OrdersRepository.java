package com.dominikcebula.spring.ai.orders.orders;

import com.dominikcebula.spring.ai.orders.api.orders.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrdersRepository {

    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public Optional<Order> findByOrderId(UUID orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public Order save(Order order) {
        orders.put(order.orderId(), order);
        return order;
    }
}
