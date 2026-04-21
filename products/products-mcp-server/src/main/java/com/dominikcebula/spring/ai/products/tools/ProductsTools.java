package com.dominikcebula.spring.ai.products.tools;

import com.dominikcebula.spring.ai.products.api.products.Product;
import com.dominikcebula.spring.ai.products.api.products.ProductsApi;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductsTools {
    private final ProductsApi productsApi;

    public ProductsTools(ProductsApi productsApi) {
        this.productsApi = productsApi;
    }

    @McpTool(description = "Get all products from the catalog, optionally filtered by category and/or a search term matching product name, SKU, or tags")
    public List<Product> getAllProducts(
            @McpToolParam(required = false, description = "Product category (e.g. Laptops, Monitors, Keyboards, Mice, Headsets, Tablets, Smartphones, Smartwatches, Cameras, Audio, Accessories)")
            String category,
            @McpToolParam(required = false, description = "Search term matched against product name, SKU, or tags")
            String search) {
        return productsApi.getAllProducts(category, search);
    }

    @McpTool(description = "Get a product by its numeric identifier")
    public Product getProductById(
            @McpToolParam(description = "Product identifier")
            Long id) {
        return productsApi.getProductById(id);
    }
}
