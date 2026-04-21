package com.dominikcebula.spring.ai.products.products;

import com.dominikcebula.spring.ai.products.api.products.Product;
import com.dominikcebula.spring.ai.products.api.products.ProductsApi;
import com.dominikcebula.spring.ai.products.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductsController implements ProductsApi {

    private final ProductsService productsService;

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Override
    public List<Product> getAllProducts(String category, String search) {
        if (category != null && search != null) {
            return productsService.searchProductsByCategory(category, search);
        }
        if (category != null) {
            return productsService.getProductsByCategory(category);
        }
        if (search != null) {
            return productsService.searchProducts(search);
        }

        return productsService.getAllProducts();
    }

    @Override
    public Product getProductById(Long id) {
        return productsService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
}
