package com.dominikcebula.spring.ai.orders.orders;

import com.dominikcebula.spring.ai.orders.api.orders.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final OrderNumberGenerator orderNumberGenerator;

    public OrdersService(OrdersRepository ordersRepository, OrderNumberGenerator orderNumberGenerator) {
        this.ordersRepository = ordersRepository;
        this.orderNumberGenerator = orderNumberGenerator;
    }

    public List<Order> getAllOrders() {
        return ordersRepository.findAll();
    }

    public Optional<Order> getOrderById(UUID orderId) {
        return ordersRepository.findByOrderId(orderId);
    }

    public Order createOrder(CreateOrderRequest request) {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalValue = calculateTotalValue(request.items());

        Order order = new Order(
                UUID.randomUUID(),
                orderNumberGenerator.next(),
                now,
                request.customerName(),
                request.customerEmail(),
                OrderStatus.CREATED,
                totalValue,
                request.items(),
                now,
                now
        );

        return ordersRepository.save(order);
    }

    public Optional<Order> updateOrder(UUID orderId, UpdateOrderRequest request) {
        return ordersRepository.findByOrderId(orderId)
                .filter(this::isOrderModifiable)
                .map(existingOrder -> {
                    BigDecimal newTotalValue = calculateTotalValue(request.items());
                    Order updatedOrder = new OrderFactory(existingOrder).withUpdatedDetails(
                            request.customerName(),
                            request.customerEmail(),
                            request.items(),
                            newTotalValue
                    );
                    return ordersRepository.save(updatedOrder);
                });
    }

    public Optional<Order> cancelOrder(UUID orderId) {
        return ordersRepository.findByOrderId(orderId)
                .filter(this::isOrderModifiable)
                .map(order -> {
                    Order cancelledOrder = new OrderFactory(order).withStatus(OrderStatus.CANCELLED);
                    return ordersRepository.save(cancelledOrder);
                });
    }

    private boolean isOrderModifiable(Order order) {
        return order.status() != OrderStatus.CANCELLED && order.status() != OrderStatus.COMPLETED;
    }

    private BigDecimal calculateTotalValue(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
