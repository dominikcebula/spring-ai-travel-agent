package com.dominikcebula.spring.ai.orders.tools;

import com.dominikcebula.spring.ai.orders.api.orders.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = "orders.api.base-uri=http://localhost:0")
class OrdersToolsMcpTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private OrdersApi ordersApi;

    private McpSyncClient mcpClient;

    private static final ObjectMapper OBJECT_MAPPER = buildObjectMapper();

    @BeforeEach
    void setUp() {
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport
                .builder("http://localhost:" + port)
                .build();
        mcpClient = McpClient.sync(transport).build();
        mcpClient.initialize();
    }

    @AfterEach
    void tearDown() {
        if (mcpClient != null) {
            mcpClient.close();
        }
    }

    @Test
    void shouldExposeAllOrderTools() {
        // given
        // (server is started with OrdersTools registered)

        // when
        ListToolsResult listToolsResult = mcpClient.listTools();

        // then
        assertThat(listToolsResult.tools())
                .extracting(Tool::name)
                .contains("getAllOrders", "getOrder", "createOrder", "updateOrder", "cancelOrder");
    }

    @Test
    void shouldCallGetAllOrdersAndReturnOrdersFromUnderlyingApi() throws Exception {
        // given
        Order firstOrder = sampleOrder(UUID.randomUUID(), "ORD-20260422-0001",
                "Alice", "alice@example.com", OrderStatus.CREATED,
                List.of(new OrderItem(1L, "Laptop", 1, new BigDecimal("1200.00"))));
        Order secondOrder = sampleOrder(UUID.randomUUID(), "ORD-20260422-0002",
                "Bob", "bob@example.com", OrderStatus.CREATED,
                List.of(new OrderItem(2L, "Mouse", 2, new BigDecimal("25.50"))));
        given(ordersApi.getAllOrders()).willReturn(List.of(firstOrder, secondOrder));

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllOrders", Map.of()));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        verify(ordersApi).getAllOrders();
        List<Order> returnedOrders = extractOrderList(result);
        assertThat(returnedOrders)
                .extracting(Order::orderNumber, Order::customerName)
                .containsExactlyInAnyOrder(
                        tuple("ORD-20260422-0001", "Alice"),
                        tuple("ORD-20260422-0002", "Bob")
                );
    }

    @Test
    void shouldCallGetOrderAndReturnFullOrder() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = sampleOrder(orderId, "ORD-20260422-0042",
                "Carol", "carol@example.com", OrderStatus.CREATED,
                List.of(new OrderItem(5L, "Headphones", 1, new BigDecimal("199.99"))));
        given(ordersApi.getOrder(orderId)).willReturn(order);

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getOrder",
                Map.of("orderId", orderId.toString())));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        verify(ordersApi).getOrder(orderId);
        Order returned = extractOrder(result);
        assertThat(returned.orderId()).isEqualTo(orderId);
        assertThat(returned.orderNumber()).isEqualTo("ORD-20260422-0042");
        assertThat(returned.customerName()).isEqualTo("Carol");
        assertThat(returned.customerEmail()).isEqualTo("carol@example.com");
        assertThat(returned.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(returned.totalValue()).isEqualByComparingTo("199.99");
        assertThat(returned.items()).singleElement()
                .extracting(OrderItem::productId, OrderItem::productName, OrderItem::quantity)
                .containsExactly(5L, "Headphones", 1);
    }

    @Test
    void shouldReportErrorWhenGetOrderFailsForMissingOrder() {
        // given
        UUID missingOrderId = UUID.randomUUID();
        given(ordersApi.getOrder(missingOrderId))
                .willThrow(new RuntimeException("Order not found"));

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getOrder",
                Map.of("orderId", missingOrderId.toString())));

        // then
        assertThat(result.isError()).isTrue();
    }

    @Test
    void shouldCallCreateOrderAndForwardRequestToUnderlyingApi() throws Exception {
        // given
        UUID createdOrderId = UUID.randomUUID();
        Order createdOrder = sampleOrder(createdOrderId, "ORD-20260422-0100",
                "Dave", "dave@example.com", OrderStatus.CREATED,
                List.of(new OrderItem(1L, "Laptop", 1, new BigDecimal("1200.00"))));
        given(ordersApi.createOrder(any(CreateOrderRequest.class))).willReturn(createdOrder);

        Map<String, Object> request = new HashMap<>();
        request.put("customerName", "Dave");
        request.put("customerEmail", "dave@example.com");
        request.put("items", List.of(Map.of(
                "productId", 1,
                "productName", "Laptop",
                "quantity", 1,
                "unitPrice", 1200.00
        )));
        Map<String, Object> args = Map.of("request", request);

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("createOrder", args));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        ArgumentCaptor<CreateOrderRequest> captor = ArgumentCaptor.forClass(CreateOrderRequest.class);
        verify(ordersApi).createOrder(captor.capture());
        CreateOrderRequest forwarded = captor.getValue();
        assertThat(forwarded.customerName()).isEqualTo("Dave");
        assertThat(forwarded.customerEmail()).isEqualTo("dave@example.com");
        assertThat(forwarded.items()).singleElement()
                .satisfies(item -> {
                    assertThat(item.productId()).isEqualTo(1L);
                    assertThat(item.productName()).isEqualTo("Laptop");
                    assertThat(item.quantity()).isEqualTo(1);
                    assertThat(item.unitPrice()).isEqualByComparingTo("1200.00");
                });

        Order returned = extractOrder(result);
        assertThat(returned.orderId()).isEqualTo(createdOrderId);
        assertThat(returned.orderNumber()).isEqualTo("ORD-20260422-0100");
        assertThat(returned.status()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldCallUpdateOrderAndForwardRequestToUnderlyingApi() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        Order updatedOrder = sampleOrder(orderId, "ORD-20260422-0100",
                "Dave Jr.", "dave.jr@example.com", OrderStatus.UPDATED,
                List.of(new OrderItem(1L, "Laptop", 2, new BigDecimal("1200.00"))));
        given(ordersApi.updateOrder(eq(orderId), any(UpdateOrderRequest.class))).willReturn(updatedOrder);

        Map<String, Object> request = new HashMap<>();
        request.put("customerName", "Dave Jr.");
        request.put("customerEmail", "dave.jr@example.com");
        request.put("items", List.of(Map.of(
                "productId", 1,
                "productName", "Laptop",
                "quantity", 2,
                "unitPrice", 1200.00
        )));
        Map<String, Object> args = Map.of(
                "orderId", orderId.toString(),
                "request", request
        );

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("updateOrder", args));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        ArgumentCaptor<UpdateOrderRequest> captor = ArgumentCaptor.forClass(UpdateOrderRequest.class);
        verify(ordersApi).updateOrder(eq(orderId), captor.capture());
        UpdateOrderRequest forwarded = captor.getValue();
        assertThat(forwarded.customerName()).isEqualTo("Dave Jr.");
        assertThat(forwarded.items()).singleElement()
                .satisfies(item -> {
                    assertThat(item.quantity()).isEqualTo(2);
                    assertThat(item.unitPrice()).isEqualByComparingTo("1200.00");
                });

        Order returned = extractOrder(result);
        assertThat(returned.orderId()).isEqualTo(orderId);
        assertThat(returned.status()).isEqualTo(OrderStatus.UPDATED);
        assertThat(returned.customerName()).isEqualTo("Dave Jr.");
    }

    @Test
    void shouldReportErrorWhenUpdateOrderFailsForMissingOrder() {
        // given
        UUID missingOrderId = UUID.randomUUID();
        given(ordersApi.updateOrder(eq(missingOrderId), any(UpdateOrderRequest.class)))
                .willThrow(new RuntimeException("Order not found"));
        Map<String, Object> request = Map.of(
                "customerName", "Ghost",
                "customerEmail", "ghost@example.com",
                "items", List.of(Map.of(
                        "productId", 1,
                        "productName", "Nothing",
                        "quantity", 1,
                        "unitPrice", 1.00
                ))
        );
        Map<String, Object> args = Map.of(
                "orderId", missingOrderId.toString(),
                "request", request
        );

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("updateOrder", args));

        // then
        assertThat(result.isError()).isTrue();
    }

    @Test
    void shouldCallCancelOrderAndReturnCancelledOrder() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        Order cancelledOrder = sampleOrder(orderId, "ORD-20260422-0100",
                "Eve", "eve@example.com", OrderStatus.CANCELLED,
                List.of(new OrderItem(7L, "Speaker", 1, new BigDecimal("150.00"))));
        given(ordersApi.cancelOrder(orderId)).willReturn(cancelledOrder);

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("cancelOrder",
                Map.of("orderId", orderId.toString())));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        verify(ordersApi).cancelOrder(orderId);
        Order returned = extractOrder(result);
        assertThat(returned.orderId()).isEqualTo(orderId);
        assertThat(returned.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldReportErrorWhenCancelOrderFailsForMissingOrder() {
        // given
        UUID missingOrderId = UUID.randomUUID();
        given(ordersApi.cancelOrder(missingOrderId))
                .willThrow(new RuntimeException("Order not found"));

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("cancelOrder",
                Map.of("orderId", missingOrderId.toString())));

        // then
        assertThat(result.isError()).isTrue();
    }

    private static Order sampleOrder(UUID orderId, String orderNumber,
                                     String customerName, String customerEmail,
                                     OrderStatus status, List<OrderItem> items) {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalValue = items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Order(orderId, orderNumber, now, customerName, customerEmail,
                status, totalValue, items, now, now);
    }

    private static List<Order> extractOrderList(CallToolResult result) throws Exception {
        String json = extractPayloadJson(result);
        return OBJECT_MAPPER.readValue(json, new TypeReference<>() {
        });
    }

    private static Order extractOrder(CallToolResult result) throws Exception {
        String json = extractPayloadJson(result);
        return OBJECT_MAPPER.readValue(json, Order.class);
    }

    private static String extractPayloadJson(CallToolResult result) throws Exception {
        String text = ((TextContent) result.content().getFirst()).text();
        JsonNode node = OBJECT_MAPPER.readTree(text);
        if (node.isObject() && node.has("result")) {
            return OBJECT_MAPPER.writeValueAsString(node.get("result"));
        }
        return text;
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
