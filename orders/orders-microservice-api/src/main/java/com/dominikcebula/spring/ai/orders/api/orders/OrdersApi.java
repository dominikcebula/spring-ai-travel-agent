package com.dominikcebula.spring.ai.orders.api.orders;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import java.util.List;
import java.util.UUID;

@HttpExchange("/api/v1/orders")
public interface OrdersApi {

    @GetExchange
    List<Order> getAllOrders();

    @GetExchange("/{orderId}")
    Order getOrder(@PathVariable UUID orderId);

    @PostExchange
    Order createOrder(@RequestBody CreateOrderRequest request);

    @PutExchange("/{orderId}")
    Order updateOrder(@PathVariable UUID orderId, @RequestBody UpdateOrderRequest request);

    @DeleteExchange("/{orderId}")
    Order cancelOrder(@PathVariable UUID orderId);
}
