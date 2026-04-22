package com.dominikcebula.spring.ai.systemtests;

import com.dominikcebula.spring.ai.orders.OrdersClientFactory;
import com.dominikcebula.spring.ai.orders.api.orders.Order;
import com.dominikcebula.spring.ai.orders.api.orders.OrderItem;
import com.dominikcebula.spring.ai.orders.api.orders.OrderStatus;
import com.dominikcebula.spring.ai.orders.api.orders.OrdersApi;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrdersToolsMcpTest {

    private static final String ORDERS_MCP_URL = "http://localhost:8031";
    private static final String ORDERS_REST_URL = "http://localhost:8030";

    private static final ObjectMapper OBJECT_MAPPER = buildObjectMapper();

    private McpSyncClient mcpClient;
    private OrdersApi ordersRestClient;

    @BeforeEach
    void setUp() {
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport
                .builder(ORDERS_MCP_URL)
                .build();
        mcpClient = McpClient.sync(transport).build();
        mcpClient.initialize();

        ordersRestClient = OrdersClientFactory.newOrdersApiClient(ORDERS_REST_URL);
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
        // (orders-mcp-server is running via docker compose)

        // when
        ListToolsResult listToolsResult = mcpClient.listTools();

        // then
        assertThat(listToolsResult.tools())
                .extracting(Tool::name)
                .contains("getAllOrders", "getOrder", "createOrder", "updateOrder", "cancelOrder");
    }

    @Test
    void shouldCreateOrderThroughMcpAndPersistItInOrdersMicroservice() throws Exception {
        // given
        Map<String, Object> createRequest = Map.of(
                "customerName", "Alice System",
                "customerEmail", "alice.system@example.com",
                "items", List.of(Map.of(
                        "productId", 1,
                        "productName", "Wireless Gaming Mouse",
                        "quantity", 2,
                        "unitPrice", 59.99
                ))
        );

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("createOrder",
                Map.of("request", createRequest)));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        Order createdOrder = extractOrder(result);
        assertThat(createdOrder.orderId()).isNotNull();
        assertThat(createdOrder.customerName()).isEqualTo("Alice System");
        assertThat(createdOrder.status()).isEqualTo(OrderStatus.CREATED);

        Order persistedOrder = ordersRestClient.getOrder(createdOrder.orderId());
        assertThat(persistedOrder.orderId()).isEqualTo(createdOrder.orderId());
        assertThat(persistedOrder.customerName()).isEqualTo("Alice System");
        assertThat(persistedOrder.customerEmail()).isEqualTo("alice.system@example.com");
        assertThat(persistedOrder.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(persistedOrder.totalValue()).isEqualByComparingTo(new BigDecimal("119.98"));
        assertThat(persistedOrder.items())
                .extracting(OrderItem::productId, OrderItem::productName, OrderItem::quantity)
                .containsExactly(tuple(1L, "Wireless Gaming Mouse", 2));
    }

    @Test
    void shouldReturnAllOrdersThroughMcpIncludingOnesCreatedViaMcp() throws Exception {
        // given
        UUID firstOrderId = createOrderViaMcp("Bob System", "bob.system@example.com",
                2L, "Ergonomic Keyboard", 1, new BigDecimal("89.50"));
        UUID secondOrderId = createOrderViaMcp("Carol System", "carol.system@example.com",
                21L, "Apex Pro 15 Ultrabook", 1, new BigDecimal("1499.99"));

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllOrders", Map.of()));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        List<Order> ordersFromMcp = extractOrderList(result);
        assertThat(ordersFromMcp)
                .extracting(Order::orderId)
                .contains(firstOrderId, secondOrderId);
    }

    @Test
    void shouldReturnSingleOrderByIdThroughMcp() throws Exception {
        // given
        UUID orderId = createOrderViaMcp("Dave System", "dave.system@example.com",
                48L, "Noise Cancelling Headphones", 1, new BigDecimal("199.99"));

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getOrder",
                Map.of("orderId", orderId.toString())));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        Order orderFromMcp = extractOrder(result);
        assertThat(orderFromMcp.orderId()).isEqualTo(orderId);
        assertThat(orderFromMcp.customerName()).isEqualTo("Dave System");
        assertThat(orderFromMcp.items())
                .extracting(OrderItem::productId, OrderItem::quantity)
                .containsExactly(tuple(48L, 1));
    }

    @Test
    void shouldUpdateOrderThroughMcpAndReflectChangesInOrdersMicroservice() throws Exception {
        // given
        UUID orderId = createOrderViaMcp("Eve System", "eve.system@example.com",
                1L, "Wireless Gaming Mouse", 1, new BigDecimal("59.99"));

        Map<String, Object> updateRequest = Map.of(
                "customerName", "Eve Updated",
                "customerEmail", "eve.updated@example.com",
                "items", List.of(Map.of(
                        "productId", 1,
                        "productName", "Wireless Gaming Mouse",
                        "quantity", 3,
                        "unitPrice", 59.99
                ))
        );

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("updateOrder",
                Map.of(
                        "orderId", orderId.toString(),
                        "request", updateRequest
                )));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        Order updatedOrderFromMcp = extractOrder(result);
        assertThat(updatedOrderFromMcp.orderId()).isEqualTo(orderId);
        assertThat(updatedOrderFromMcp.status()).isEqualTo(OrderStatus.UPDATED);

        Order persistedOrder = ordersRestClient.getOrder(orderId);
        assertThat(persistedOrder.customerName()).isEqualTo("Eve Updated");
        assertThat(persistedOrder.customerEmail()).isEqualTo("eve.updated@example.com");
        assertThat(persistedOrder.status()).isEqualTo(OrderStatus.UPDATED);
        assertThat(persistedOrder.items())
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.productId()).isEqualTo(1L);
                    assertThat(item.quantity()).isEqualTo(3);
                    assertThat(item.unitPrice()).isEqualByComparingTo("59.99");
                });
        assertThat(persistedOrder.totalValue()).isEqualByComparingTo(new BigDecimal("179.97"));
    }

    @Test
    void shouldCancelOrderThroughMcpAndReflectCancellationInOrdersMicroservice() throws Exception {
        // given
        UUID orderId = createOrderViaMcp("Frank System", "frank.system@example.com",
                25L, "Business Laptop Pro", 1, new BigDecimal("1299.00"));

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("cancelOrder",
                Map.of("orderId", orderId.toString())));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        Order cancelledOrderFromMcp = extractOrder(result);
        assertThat(cancelledOrderFromMcp.orderId()).isEqualTo(orderId);
        assertThat(cancelledOrderFromMcp.status()).isEqualTo(OrderStatus.CANCELLED);

        Order persistedOrder = ordersRestClient.getOrder(orderId);
        assertThat(persistedOrder.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldReportErrorWhenGettingOrderWithUnknownId() {
        // given
        UUID unknownOrderId = UUID.randomUUID();

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getOrder",
                Map.of("orderId", unknownOrderId.toString())));

        // then
        assertThat(result.isError()).isTrue();
        assertThatThrownBy(() -> ordersRestClient.getOrder(unknownOrderId))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    private UUID createOrderViaMcp(String customerName, String customerEmail,
                                   Long productId, String productName,
                                   int quantity, BigDecimal unitPrice) throws Exception {
        Map<String, Object> createRequest = Map.of(
                "customerName", customerName,
                "customerEmail", customerEmail,
                "items", List.of(Map.of(
                        "productId", productId,
                        "productName", productName,
                        "quantity", quantity,
                        "unitPrice", unitPrice
                ))
        );
        CallToolResult result = mcpClient.callTool(new CallToolRequest("createOrder",
                Map.of("request", createRequest)));
        assertThat(result.isError()).isNotEqualTo(true);
        return extractOrder(result).orderId();
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
