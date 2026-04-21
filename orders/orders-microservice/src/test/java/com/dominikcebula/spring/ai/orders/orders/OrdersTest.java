package com.dominikcebula.spring.ai.orders.orders;

import com.dominikcebula.spring.ai.orders.OrdersClientFactory;
import com.dominikcebula.spring.ai.orders.api.orders.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrdersTest {

    @LocalServerPort
    private int port;

    private OrdersApi ordersClient;

    @BeforeEach
    void setUp() {
        ordersClient = OrdersClientFactory.newOrdersApiClient("http://localhost:" + port);
    }

    @Test
    void shouldCreateSingleItemOrder() {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                "Alice Johnson",
                "alice@example.com",
                List.of(new OrderItem(1L, "Laptop", 1, new BigDecimal("1200.00")))
        );

        // when
        Order created = ordersClient.createOrder(request);

        // then
        assertThat(created.orderId()).isNotNull();
        assertThat(created.orderNumber()).matches("ORD-\\d{8}-\\d{4}");
        assertThat(created.orderDate()).isNotNull();
        assertThat(created.customerName()).isEqualTo("Alice Johnson");
        assertThat(created.customerEmail()).isEqualTo("alice@example.com");
        assertThat(created.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(created.totalValue()).isEqualByComparingTo("1200.00");
        assertThat(created.items()).containsExactly(
                new OrderItem(1L, "Laptop", 1, new BigDecimal("1200.00"))
        );
        assertThat(created.createdAt()).isNotNull();
        assertThat(created.updatedAt()).isNotNull();
    }

    @Test
    void shouldComputeTotalAsSumOfQuantityTimesUnitPriceForMultiItemOrder() {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                "Bob Smith",
                "bob@example.com",
                List.of(
                        new OrderItem(1L, "Mouse", 2, new BigDecimal("25.50")),
                        new OrderItem(2L, "Keyboard", 3, new BigDecimal("80.00")),
                        new OrderItem(3L, "Monitor", 1, new BigDecimal("350.00"))
                )
        );

        // when
        Order created = ordersClient.createOrder(request);

        // then
        // 2*25.50 + 3*80.00 + 1*350.00 = 51.00 + 240.00 + 350.00 = 641.00
        assertThat(created.totalValue()).isEqualByComparingTo("641.00");
        assertThat(created.items()).hasSize(3);
    }

    @Test
    void shouldRetrieveOrderById() {
        // given
        Order created = ordersClient.createOrder(new CreateOrderRequest(
                "Carol White",
                "carol@example.com",
                List.of(new OrderItem(10L, "Headphones", 1, new BigDecimal("199.99")))
        ));

        // when
        Order fetched = ordersClient.getOrder(created.orderId());

        // then
        assertThat(fetched).isEqualTo(created);
    }

    @Test
    void shouldReturnNotFoundWhenRetrievingMissingOrder() {
        // given
        UUID missingOrderId = UUID.randomUUID();

        // when / then
        assertThatThrownBy(() -> ordersClient.getOrder(missingOrderId))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void shouldListAllOrdersIncludingRecentlyCreatedOnes() {
        // given
        Order first = ordersClient.createOrder(new CreateOrderRequest(
                "Dave Brown", "dave@example.com",
                List.of(new OrderItem(1L, "Book", 1, new BigDecimal("19.99")))
        ));
        Order second = ordersClient.createOrder(new CreateOrderRequest(
                "Eve Davis", "eve@example.com",
                List.of(new OrderItem(2L, "Pen", 5, new BigDecimal("2.00")))
        ));

        // when
        List<Order> orders = ordersClient.getAllOrders();

        // then
        assertThat(orders).extracting(Order::orderId)
                .contains(first.orderId(), second.orderId());
    }

    @Test
    void shouldUpdateOrderDetailsRecalculateTotalAndPreserveIdentity() {
        // given
        Order created = ordersClient.createOrder(new CreateOrderRequest(
                "Frank Miller", "frank@example.com",
                List.of(new OrderItem(1L, "Tablet", 1, new BigDecimal("500.00")))
        ));
        UpdateOrderRequest updateRequest = new UpdateOrderRequest(
                "Frank Miller Jr.",
                "frank.jr@example.com",
                List.of(
                        new OrderItem(1L, "Tablet", 2, new BigDecimal("500.00")),
                        new OrderItem(5L, "Stylus", 1, new BigDecimal("49.99"))
                )
        );

        // when
        Order updated = ordersClient.updateOrder(created.orderId(), updateRequest);

        // then
        assertThat(updated.orderId()).isEqualTo(created.orderId());
        assertThat(updated.orderNumber()).isEqualTo(created.orderNumber());
        assertThat(updated.createdAt()).isEqualTo(created.createdAt());
        assertThat(updated.status()).isEqualTo(OrderStatus.UPDATED);
        assertThat(updated.customerName()).isEqualTo("Frank Miller Jr.");
        assertThat(updated.customerEmail()).isEqualTo("frank.jr@example.com");
        assertThat(updated.totalValue()).isEqualByComparingTo("1049.99");
        assertThat(updated.items()).hasSize(2);
        assertThat(updated.updatedAt()).isAfterOrEqualTo(created.updatedAt());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingOrder() {
        // given
        UUID missingOrderId = UUID.randomUUID();
        UpdateOrderRequest updateRequest = new UpdateOrderRequest(
                "Ghost", "ghost@example.com",
                List.of(new OrderItem(1L, "Nothing", 1, new BigDecimal("1.00")))
        );

        // when / then
        assertThatThrownBy(() -> ordersClient.updateOrder(missingOrderId, updateRequest))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void shouldCancelOrderAndPreserveOriginalDetails() {
        // given
        Order created = ordersClient.createOrder(new CreateOrderRequest(
                "Grace Lee", "grace@example.com",
                List.of(new OrderItem(1L, "Camera", 1, new BigDecimal("799.00")))
        ));

        // when
        Order cancelled = ordersClient.cancelOrder(created.orderId());

        // then
        assertThat(cancelled.orderId()).isEqualTo(created.orderId());
        assertThat(cancelled.orderNumber()).isEqualTo(created.orderNumber());
        assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(cancelled.customerName()).isEqualTo(created.customerName());
        assertThat(cancelled.customerEmail()).isEqualTo(created.customerEmail());
        assertThat(cancelled.totalValue()).isEqualByComparingTo(created.totalValue());
        assertThat(cancelled.items()).isEqualTo(created.items());
        assertThat(cancelled.createdAt()).isEqualTo(created.createdAt());
    }

    @Test
    void shouldReturnNotFoundWhenCancellingMissingOrder() {
        // given
        UUID missingOrderId = UUID.randomUUID();

        // when / then
        assertThatThrownBy(() -> ordersClient.cancelOrder(missingOrderId))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void shouldRejectUpdateOfCancelledOrder() {
        // given
        Order created = ordersClient.createOrder(new CreateOrderRequest(
                "Henry Wilson", "henry@example.com",
                List.of(new OrderItem(1L, "Speaker", 1, new BigDecimal("150.00")))
        ));
        ordersClient.cancelOrder(created.orderId());
        UpdateOrderRequest updateRequest = new UpdateOrderRequest(
                "Henry Wilson", "henry@example.com",
                List.of(new OrderItem(1L, "Speaker", 2, new BigDecimal("150.00")))
        );

        // when / then
        assertThatThrownBy(() -> ordersClient.updateOrder(created.orderId(), updateRequest))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void shouldRejectCancellationOfAlreadyCancelledOrder() {
        // given
        Order created = ordersClient.createOrder(new CreateOrderRequest(
                "Ivy Thomas", "ivy@example.com",
                List.of(new OrderItem(1L, "Lamp", 1, new BigDecimal("45.00")))
        ));
        ordersClient.cancelOrder(created.orderId());

        // when / then
        assertThatThrownBy(() -> ordersClient.cancelOrder(created.orderId()))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void shouldAssignSequentialOrderNumbersOnSameDay() {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                "Jack Anderson", "jack@example.com",
                List.of(new OrderItem(1L, "Charger", 1, new BigDecimal("25.00")))
        );

        // when
        Order first = ordersClient.createOrder(request);
        Order second = ordersClient.createOrder(request);
        Order third = ordersClient.createOrder(request);

        // then
        assertThat(first.orderNumber()).matches("ORD-\\d{8}-\\d{4}");
        assertThat(second.orderNumber()).matches("ORD-\\d{8}-\\d{4}");
        assertThat(third.orderNumber()).matches("ORD-\\d{8}-\\d{4}");
        int firstSeq = extractSequence(first.orderNumber());
        int secondSeq = extractSequence(second.orderNumber());
        int thirdSeq = extractSequence(third.orderNumber());
        assertThat(secondSeq).isEqualTo(firstSeq + 1);
        assertThat(thirdSeq).isEqualTo(secondSeq + 1);
    }

    private static int extractSequence(String orderNumber) {
        return Integer.parseInt(orderNumber.substring(orderNumber.lastIndexOf('-') + 1));
    }
}
