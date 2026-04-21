package com.dominikcebula.spring.ai.products.products;

import com.dominikcebula.spring.ai.products.api.products.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productsRepository.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productsRepository.findByCategory(category);
    }

    public List<Product> searchProducts(String searchTerm) {
        return productsRepository.search(searchTerm);
    }

    public List<Product> searchProductsByCategory(String category, String searchTerm) {
        return productsRepository.searchByCategory(category, searchTerm);
    }
}
