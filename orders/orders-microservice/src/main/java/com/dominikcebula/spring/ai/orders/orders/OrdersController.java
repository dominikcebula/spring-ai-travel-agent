package com.dominikcebula.spring.ai.orders.orders;

import com.dominikcebula.spring.ai.orders.api.orders.CreateOrderRequest;
import com.dominikcebula.spring.ai.orders.api.orders.Order;
import com.dominikcebula.spring.ai.orders.api.orders.OrdersApi;
import com.dominikcebula.spring.ai.orders.api.orders.UpdateOrderRequest;
import com.dominikcebula.spring.ai.orders.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OrdersController implements OrdersApi {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @Override
    public List<Order> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @Override
    public Order getOrder(UUID orderId) {
        return ordersService.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(CreateOrderRequest request) {
        return ordersService.createOrder(request);
    }

    @Override
    public Order updateOrder(UUID orderId, UpdateOrderRequest request) {
        return ordersService.updateOrder(orderId, request)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    @Override
    public Order cancelOrder(UUID orderId) {
        return ordersService.cancelOrder(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }
}
