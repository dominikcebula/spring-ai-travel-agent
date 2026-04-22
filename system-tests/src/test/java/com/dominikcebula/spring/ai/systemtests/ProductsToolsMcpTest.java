package com.dominikcebula.spring.ai.systemtests;

import com.dominikcebula.spring.ai.products.ProductsClientFactory;
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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductsToolsMcpTest {

    private static final String PRODUCTS_MCP_URL = "http://localhost:8021";
    private static final String PRODUCTS_REST_URL = "http://localhost:8020";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private McpSyncClient mcpClient;
    private ProductsApi productsRestClient;

    @BeforeEach
    void setUp() {
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport
                .builder(PRODUCTS_MCP_URL)
                .build();
        mcpClient = McpClient.sync(transport).build();
        mcpClient.initialize();

        productsRestClient = ProductsClientFactory.newProductsApiClient(PRODUCTS_REST_URL);
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
        // (products-mcp-server is running via docker compose)

        // when
        ListToolsResult listToolsResult = mcpClient.listTools();

        // then
        assertThat(listToolsResult.tools())
                .extracting(Tool::name)
                .contains("getAllProducts", "getProductById");
    }

    @Test
    void shouldReturnCatalogWhenCallingGetAllProductsTool() throws Exception {
        // given
        // (products-microservice seeded with catalog)

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllProducts", Map.of()));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        List<Product> productsFromMcp = extractProductList(result);
        assertThat(productsFromMcp).isNotEmpty();
        assertThat(productsFromMcp)
                .extracting(Product::id)
                .contains(1L, 21L);

        List<Product> productsFromRest = productsRestClient.getAllProducts(null, null);
        assertThat(productsFromMcp)
                .extracting(Product::id)
                .containsExactlyInAnyOrderElementsOf(
                        productsFromRest.stream().map(Product::id).toList());
    }

    @Test
    void shouldFilterByCategoryWhenCallingGetAllProductsTool() throws Exception {
        // given
        String category = "Laptops";

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllProducts",
                Map.of("category", category)));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        List<Product> productsFromMcp = extractProductList(result);
        assertThat(productsFromMcp).isNotEmpty();
        assertThat(productsFromMcp)
                .allSatisfy(product -> assertThat(product.category()).isEqualTo("Laptops"));

        List<Product> productsFromRest = productsRestClient.getAllProducts(category, null);
        assertThat(productsFromMcp)
                .extracting(Product::id)
                .containsExactlyInAnyOrderElementsOf(
                        productsFromRest.stream().map(Product::id).toList());
    }

    @Test
    void shouldSearchBySearchTermWhenCallingGetAllProductsTool() throws Exception {
        // given
        String search = "ultrabook";

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllProducts",
                Map.of("search", search)));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        List<Product> productsFromMcp = extractProductList(result);
        assertThat(productsFromMcp).isNotEmpty();
        assertThat(productsFromMcp).anySatisfy(product ->
                assertThat(product.tags()).anyMatch(tag -> tag.toLowerCase().contains("ultrabook")));
    }

    @Test
    void shouldReturnEmptyListForUnknownCategoryWhenCallingGetAllProductsTool() throws Exception {
        // given
        String unknownCategory = "NonExistentCategory";

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getAllProducts",
                Map.of("category", unknownCategory)));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        List<Product> productsFromMcp = extractProductList(result);
        assertThat(productsFromMcp).isEmpty();
    }

    @Test
    void shouldReturnProductByIdWhenCallingGetProductByIdTool() throws Exception {
        // given
        Long productId = 1L;

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getProductById",
                Map.of("id", productId)));

        // then
        assertThat(result.isError()).isNotEqualTo(true);
        Product productFromMcp = extractProduct(result);
        Product productFromRest = productsRestClient.getProductById(productId);
        assertThat(productFromMcp.id()).isEqualTo(productFromRest.id());
        assertThat(productFromMcp.name()).isEqualTo(productFromRest.name());
        assertThat(productFromMcp.sku()).isEqualTo(productFromRest.sku());
        assertThat(productFromMcp.category()).isEqualTo(productFromRest.category());
        assertThat(productFromMcp.price()).isEqualByComparingTo(productFromRest.price());
    }

    @Test
    void shouldReportErrorWhenCallingGetProductByIdWithMissingId() {
        // given
        Long missingId = 99999L;

        // when
        CallToolResult result = mcpClient.callTool(new CallToolRequest("getProductById",
                Map.of("id", missingId)));

        // then
        assertThat(result.isError()).isTrue();
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
