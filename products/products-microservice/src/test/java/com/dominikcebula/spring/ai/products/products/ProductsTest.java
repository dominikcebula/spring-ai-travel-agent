package com.dominikcebula.spring.ai.products.products;

import com.dominikcebula.spring.ai.products.ProductsClientFactory;
import com.dominikcebula.spring.ai.products.api.products.Product;
import com.dominikcebula.spring.ai.products.api.products.ProductsApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductsTest {

    @LocalServerPort
    private int port;

    private ProductsApi productsClient;

    @BeforeEach
    void setUp() {
        productsClient = ProductsClientFactory.newProductsApiClient("http://localhost:" + port);
    }

    @Test
    void shouldReturnProductWithAllFieldsById() {
        // given
        Long knownProductId = 1L;

        // when
        Product product = productsClient.getProductById(knownProductId);

        // then
        assertThat(product.id()).isEqualTo(1L);
        assertThat(product.name()).isEqualTo("Wireless Gaming Mouse");
        assertThat(product.price()).isEqualByComparingTo("59.99");
        assertThat(product.category()).isEqualTo("Mice");
        assertThat(product.stock()).isEqualTo(150);
        assertThat(product.sku()).isEqualTo("WGM-RGB-001");
        assertThat(product.rating()).isEqualTo(4.6);
        assertThat(product.popularity()).isEqualTo(1500);
        assertThat(product.tags()).containsExactlyInAnyOrder("gaming", "wireless", "rgb");
        assertThat(product.warehouseCountry()).isEqualTo("China");
    }

    @Test
    void shouldReturnNotFoundForMissingProduct() {
        // given
        Long missingProductId = 9999L;

        // when / then
        assertThatThrownBy(() -> productsClient.getProductById(missingProductId))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void shouldReturnAllProductsWhenNoFiltersApplied() {
        // given
        // (no filters)

        // when
        List<Product> products = productsClient.getAllProducts(null, null);

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).extracting(Product::id).contains(1L, 21L, 48L);
        assertThat(products).extracting(Product::category)
                .contains("Mice", "Laptops", "Smartphones", "Smartwatches", "Tablets");
    }

    @Test
    void shouldFilterByCategoryReturningOnlyMatchingProducts() {
        // given
        String category = "Laptops";

        // when
        List<Product> products = productsClient.getAllProducts(category, null);

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).allSatisfy(product ->
                assertThat(product.category()).isEqualTo("Laptops"));
        assertThat(products).extracting(Product::id).contains(21L, 25L, 30L);
    }

    @Test
    void shouldMatchCategoryCaseInsensitively() {
        // given
        String categoryLowerCase = "laptops";

        // when
        List<Product> products = productsClient.getAllProducts(categoryLowerCase, null);

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).allSatisfy(product ->
                assertThat(product.category()).isEqualToIgnoringCase("laptops"));
    }

    @Test
    void shouldReturnEmptyListForUnknownCategory() {
        // given
        String unknownCategory = "NonExistentCategory";

        // when
        List<Product> products = productsClient.getAllProducts(unknownCategory, null);

        // then
        assertThat(products).isEmpty();
    }

    @Test
    void shouldSearchByNameReturningMatchingProducts() {
        // given
        String searchTerm = "Gaming";

        // when
        List<Product> products = productsClient.getAllProducts(null, searchTerm);

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).anySatisfy(product ->
                assertThat(product.name()).containsIgnoringCase("Gaming"));
        assertThat(products).extracting(Product::id).contains(1L, 2L);
    }

    @Test
    void shouldSearchBySkuReturningMatchingProduct() {
        // given
        String skuFragment = "APX-PRO15";

        // when
        List<Product> products = productsClient.getAllProducts(null, skuFragment);

        // then
        assertThat(products).hasSize(1);
        assertThat(products.getFirst().id()).isEqualTo(21L);
        assertThat(products.getFirst().sku()).isEqualTo("APX-PRO15-16-512");
    }

    @Test
    void shouldSearchByTagReturningProductsTaggedWithTerm() {
        // given
        String tag = "ultrabook";

        // when
        List<Product> products = productsClient.getAllProducts(null, tag);

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).allSatisfy(product ->
                assertThat(product.tags()).anyMatch(t -> t.toLowerCase().contains("ultrabook")));
    }

    @Test
    void shouldSearchCaseInsensitively() {
        // given
        String searchTermMixedCase = "GAMING";

        // when
        List<Product> products = productsClient.getAllProducts(null, searchTermMixedCase);

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).extracting(Product::id).contains(1L, 2L);
    }

    @Test
    void shouldReturnEmptyListWhenSearchHasNoMatches() {
        // given
        String searchTermThatMatchesNothing = "nothing-matches-this-xyz-123";

        // when
        List<Product> products = productsClient.getAllProducts(null, searchTermThatMatchesNothing);

        // then
        assertThat(products).isEmpty();
    }

    @Test
    void shouldCombineCategoryAndSearchFiltersUsingAndSemantics() {
        // given
        String category = "Laptops";
        String searchTerm = "business";

        // when
        List<Product> products = productsClient.getAllProducts(category, searchTerm);

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).allSatisfy(product -> {
            assertThat(product.category()).isEqualTo("Laptops");
            boolean matchesName = product.name().toLowerCase().contains("business");
            boolean matchesSku = product.sku().toLowerCase().contains("business");
            boolean matchesTag = product.tags().stream()
                    .anyMatch(t -> t.toLowerCase().contains("business"));
            assertThat(matchesName || matchesSku || matchesTag).isTrue();
        });
    }

    @Test
    void shouldReturnEmptyListWhenCategoryAndSearchFiltersDoNotOverlap() {
        // given
        String category = "Laptops";
        String searchTermUnrelatedToCategory = "smartwatch";

        // when
        List<Product> products = productsClient.getAllProducts(category, searchTermUnrelatedToCategory);

        // then
        assertThat(products).isEmpty();
    }
}
