package com.dominikcebula.spring.ai.products.tools;

import com.dominikcebula.spring.ai.products.api.products.Product;
import com.dominikcebula.spring.ai.products.api.products.ProductsApi;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = "products.api.base-uri=http://localhost:0")
class ProductsToolsMcpTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private ProductsApi productsApi;

    private McpSyncClient mcpClient;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
    void shouldExposeGetAllProductsAndGetProductByIdTools() {
        // given
        // (server is started with ProductsTools registered)

        // when
        ListToolsResult listToolsResult = mcpClient.listTools();

        // then
        assertThat(listToolsResult.tools())
                .extracting(Tool::name)
                .contains("getAllProducts", "getProductById");
    }

    @Test
    void shouldCallGetAllProductsAndReturnCatalogFromUnderlyingApi() throws Exception {
        // given
        Product laptop = buildProduct(21L, "Apex Pro 15 Ultrabook", "1499.99", "Laptops",
                25, "APX-PRO15-16-512", 4.8, 820, List.of("ultrabook", "high-performance", "business"), "USA");
        Product mouse = buildProduct(1L, "Wireless Gaming Mouse", "59.99", "Mice",
                150, "WGM-RGB-001", 4.6, 1500, List.of("gaming", "wireless", "rgb"), "China");
        given(productsApi.getAllProducts(null, null)).willReturn(List.of(laptop, mouse));

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllProducts", Map.of()));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        verify(productsApi).getAllProducts(null, null);
        List<Product> returnedProducts = extractProductList(result);
        assertThat(returnedProducts)
                .extracting(Product::id, Product::name, Product::sku)
                .containsExactlyInAnyOrder(
                        tuple(21L, "Apex Pro 15 Ultrabook", "APX-PRO15-16-512"),
                        tuple(1L, "Wireless Gaming Mouse", "WGM-RGB-001")
                );
    }

    @Test
    void shouldForwardCategoryAndSearchArgumentsToUnderlyingApi() throws Exception {
        // given
        Product laptop = buildProduct(21L, "Apex Pro 15 Ultrabook", "1499.99", "Laptops",
                25, "APX-PRO15-16-512", 4.8, 820, List.of("ultrabook", "business"), "USA");
        given(productsApi.getAllProducts("Laptops", "business")).willReturn(List.of(laptop));
        Map<String, Object> args = new HashMap<>();
        args.put("category", "Laptops");
        args.put("search", "business");

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllProducts", args));

        // then
        verify(productsApi).getAllProducts(eq("Laptops"), eq("business"));
        List<Product> returnedProducts = extractProductList(result);
        assertThat(returnedProducts).singleElement()
                .satisfies(product -> {
                    assertThat(product.id()).isEqualTo(21L);
                    assertThat(product.category()).isEqualTo("Laptops");
                    assertThat(product.tags()).contains("business");
                });
    }

    @Test
    void shouldReturnEmptyListWhenUnderlyingApiReturnsNoProducts() throws Exception {
        // given
        given(productsApi.getAllProducts("UnknownCategory", null)).willReturn(List.of());
        Map<String, Object> args = new HashMap<>();
        args.put("category", "UnknownCategory");

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllProducts", args));

        // then
        List<Product> returnedProducts = extractProductList(result);
        assertThat(returnedProducts).isEmpty();
    }

    @Test
    void shouldCallGetProductByIdAndReturnFullProduct() throws Exception {
        // given
        Product laptop = buildProduct(21L, "Apex Pro 15 Ultrabook", "1499.99", "Laptops",
                25, "APX-PRO15-16-512", 4.8, 820, List.of("ultrabook", "high-performance", "business"), "USA");
        given(productsApi.getProductById(21L)).willReturn(laptop);

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getProductById", Map.of("id", 21L)));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        verify(productsApi).getProductById(21L);
        Product returnedProduct = extractProduct(result);
        assertThat(returnedProduct.id()).isEqualTo(21L);
        assertThat(returnedProduct.name()).isEqualTo("Apex Pro 15 Ultrabook");
        assertThat(returnedProduct.price()).isEqualByComparingTo("1499.99");
        assertThat(returnedProduct.category()).isEqualTo("Laptops");
        assertThat(returnedProduct.stock()).isEqualTo(25);
        assertThat(returnedProduct.sku()).isEqualTo("APX-PRO15-16-512");
        assertThat(returnedProduct.rating()).isEqualTo(4.8);
        assertThat(returnedProduct.popularity()).isEqualTo(820);
        assertThat(returnedProduct.tags()).containsExactlyInAnyOrder("ultrabook", "high-performance", "business");
        assertThat(returnedProduct.warehouseCountry()).isEqualTo("USA");
    }

    @Test
    void shouldReportErrorWhenUnderlyingApiThrowsForMissingProduct() {
        // given
        given(productsApi.getProductById(9999L))
                .willThrow(new RuntimeException("Product not found"));

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getProductById", Map.of("id", 9999L)));

        // then
        assertThat(result.isError()).isTrue();
    }

    private static Product buildProduct(Long id, String name, String price, String category,
                                        int stock, String sku, double rating, int popularity,
                                        List<String> tags, String warehouseCountry) {
        return new Product(id, name, new BigDecimal(price), category, stock, sku, rating, popularity, tags, warehouseCountry);
    }

    private static List<Product> extractProductList(CallToolResult result) throws Exception {
        String json = extractPayloadJson(result);
        return OBJECT_MAPPER.readValue(json, new TypeReference<>() {
        });
    }

    private static Product extractProduct(CallToolResult result) throws Exception {
        String json = extractPayloadJson(result);
        return OBJECT_MAPPER.readValue(json, Product.class);
    }

    private static String extractPayloadJson(CallToolResult result) throws Exception {
        String text = ((TextContent) result.content().getFirst()).text();
        JsonNode node = OBJECT_MAPPER.readTree(text);
        if (node.isObject() && node.has("result")) {
            return OBJECT_MAPPER.writeValueAsString(node.get("result"));
        }
        return text;
    }
}
