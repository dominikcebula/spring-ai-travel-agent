package com.dominikcebula.spring.ai.orders.tools;

import com.dominikcebula.spring.ai.orders.api.orders.CreateOrderRequest;
import com.dominikcebula.spring.ai.orders.api.orders.Order;
import com.dominikcebula.spring.ai.orders.api.orders.OrdersApi;
import com.dominikcebula.spring.ai.orders.api.orders.UpdateOrderRequest;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrdersTools {
    private final OrdersApi ordersApi;

    public OrdersTools(OrdersApi ordersApi) {
        this.ordersApi = ordersApi;
    }

    @McpTool(description = "Get all orders")
    public List<Order> getAllOrders() {
        return ordersApi.getAllOrders();
    }

    @McpTool(description = "Get an order by its identifier")
    public Order getOrder(
            @McpToolParam(description = "Order identifier (UUID)")
            UUID orderId) {
        return ordersApi.getOrder(orderId);
    }

    @McpTool(description = "Create a new order with customer details and items. Each item must include productId, productName, quantity, and unitPrice (the price snapshot taken from the product catalog at the time of ordering)")
    public Order createOrder(
            @McpToolParam(description = "Create order request containing customerName, customerEmail, and items (each with productId, productName, quantity, unitPrice)")
            CreateOrderRequest request) {
        return ordersApi.createOrder(request);
    }

    @McpTool(description = "Update an existing order with new customer details and/or items")
    public Order updateOrder(
            @McpToolParam(description = "Order identifier (UUID)")
            UUID orderId,
            @McpToolParam(description = "Update request containing customerName, customerEmail, and items (each with productId, productName, quantity, unitPrice)")
            UpdateOrderRequest request) {
        return ordersApi.updateOrder(orderId, request);
    }

    @McpTool(description = "Cancel an existing order by its identifier")
    public Order cancelOrder(
            @McpToolParam(description = "Order identifier (UUID)")
            UUID orderId) {
        return ordersApi.cancelOrder(orderId);
    }
}
