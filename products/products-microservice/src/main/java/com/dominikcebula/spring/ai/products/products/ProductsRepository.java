package com.dominikcebula.spring.ai.products.products;

import com.dominikcebula.spring.ai.products.api.products.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductsRepository {

    private final List<Product> products = initializeProducts();

    public List<Product> findAll() {
        return products;
    }

    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(product -> product.id().equals(id))
                .findFirst();
    }

    public List<Product> findByCategory(String category) {
        return products.stream()
                .filter(product -> product.category().equalsIgnoreCase(category))
                .toList();
    }

    public List<Product> search(String searchTerm) {
        String normalized = searchTerm.toLowerCase();
        return products.stream()
                .filter(product -> matchesSearch(product, normalized))
                .toList();
    }

    public List<Product> searchByCategory(String category, String searchTerm) {
        String normalized = searchTerm.toLowerCase();
        return products.stream()
                .filter(product -> product.category().equalsIgnoreCase(category))
                .filter(product -> matchesSearch(product, normalized))
                .toList();
    }

    private boolean matchesSearch(Product product, String normalizedSearchTerm) {
        if (product.name().toLowerCase().contains(normalizedSearchTerm)) {
            return true;
        }
        if (product.sku().toLowerCase().contains(normalizedSearchTerm)) {
            return true;
        }
        return product.tags().stream()
                .anyMatch(tag -> tag.toLowerCase().contains(normalizedSearchTerm));
    }

    private List<Product> initializeProducts() {
        return List.of(
                createProduct(1L, "Wireless Gaming Mouse", "59.99", "Mice", 150,
                        "WGM-RGB-001", 4.6, 1500, List.of("gaming", "wireless", "rgb"), "China"),
                createProduct(2L, "Mechanical Gaming Keyboard", "129.99", "Keyboards", 80,
                        "MGK-RGB-002", 4.7, 2100, List.of("gaming", "mechanical", "rgb"), "China"),
                createProduct(3L, "27-inch 4K Monitor", "349.99", "Monitors", 45,
                        "MON-4K27-003", 4.5, 980, List.of("4k", "monitor", "ips"), "South Korea"),
                createProduct(4L, "Noise-Cancelling Headset", "89.99", "Headsets", 120,
                        "HDS-NC-004", 4.4, 1320, List.of("noise-cancelling", "wireless", "bluetooth"), "Germany"),
                createProduct(5L, "Ergonomic Vertical Mouse", "39.99", "Mice", 95,
                        "MSE-ERG-005", 4.3, 780, List.of("ergonomic", "office", "wired"), "Germany"),
                createProduct(6L, "Compact Wireless Keyboard", "49.99", "Keyboards", 110,
                        "KBD-CMP-006", 4.2, 650, List.of("compact", "wireless", "travel"), "China"),
                createProduct(7L, "34-inch Ultrawide Monitor", "599.99", "Monitors", 30,
                        "MON-UW34-007", 4.8, 540, List.of("ultrawide", "productivity", "monitor"), "South Korea"),
                createProduct(8L, "USB-C Docking Station", "149.99", "Accessories", 75,
                        "ACC-DCK-008", 4.5, 890, List.of("docking", "usb-c", "productivity"), "Taiwan"),
                createProduct(9L, "RGB Gaming Headset", "69.99", "Headsets", 100,
                        "HDS-RGB-009", 4.3, 1450, List.of("gaming", "rgb", "wired"), "China"),
                createProduct(10L, "Trackball Mouse", "44.99", "Mice", 60,
                        "MSE-TRB-010", 4.1, 420, List.of("trackball", "ergonomic", "wireless"), "Germany"),
                createProduct(11L, "Mechanical Numpad", "34.99", "Keyboards", 85,
                        "KBD-NUM-011", 4.4, 380, List.of("numpad", "mechanical", "accessory"), "China"),
                createProduct(12L, "Portable USB Monitor", "199.99", "Monitors", 55,
                        "MON-PRT-012", 4.2, 610, List.of("portable", "usb", "travel"), "Taiwan"),
                createProduct(13L, "Webcam 1080p HD", "79.99", "Cameras", 140,
                        "CAM-HD-013", 4.3, 1760, List.of("webcam", "hd", "streaming"), "China"),
                createProduct(14L, "USB Microphone", "99.99", "Audio", 90,
                        "MIC-USB-014", 4.6, 1240, List.of("microphone", "usb", "streaming"), "USA"),
                createProduct(15L, "Large Gaming Mouse Pad", "24.99", "Accessories", 200,
                        "ACC-MPD-015", 4.5, 2300, List.of("gaming", "mousepad", "rgb"), "China"),
                createProduct(16L, "Monitor Stand with USB Hub", "59.99", "Accessories", 70,
                        "ACC-STD-016", 4.2, 510, List.of("monitor", "stand", "usb-hub"), "China"),
                createProduct(17L, "Wireless Earbuds", "79.99", "Headsets", 130,
                        "HDS-EAR-017", 4.4, 2100, List.of("wireless", "earbuds", "bluetooth"), "China"),
                createProduct(18L, "Bluetooth Keyboard and Mouse Combo", "69.99", "Keyboards", 65,
                        "KBD-CMB-018", 4.1, 740, List.of("bluetooth", "combo", "wireless"), "China"),
                createProduct(19L, "4K Webcam with Ring Light", "129.99", "Cameras", 50,
                        "CAM-4K-019", 4.7, 640, List.of("4k", "webcam", "streaming"), "China"),
                createProduct(20L, "USB Hub 7-Port", "29.99", "Accessories", 180,
                        "ACC-HUB-020", 4.3, 1980, List.of("usb", "hub", "expansion"), "China"),
                createProduct(21L, "Apex Pro 15 Ultrabook", "1499.99", "Laptops", 25,
                        "APX-PRO15-16-512", 4.8, 820, List.of("ultrabook", "high-performance", "business"), "USA"),
                createProduct(22L, "FlexEdge 14 Convertible Laptop", "1299.99", "Laptops", 30,
                        "FLX-EDG14-16-512", 4.6, 690, List.of("convertible", "touchscreen", "productivity"), "Taiwan"),
                createProduct(23L, "Nebula G14 Performance Laptop", "1599.99", "Laptops", 20,
                        "NBL-G14-32-1TB", 4.7, 540, List.of("gaming", "high-performance", "laptop"), "China"),
                createProduct(24L, "CarbonLite X1 Business Laptop", "1449.99", "Laptops", 35,
                        "CRB-X1-16-512", 4.7, 760, List.of("business", "ultrabook", "lightweight"), "Japan"),
                createProduct(25L, "AirLite 13 Ultra-Thin Laptop", "1099.99", "Laptops", 40,
                        "AIR-LT13-16-512", 4.5, 910, List.of("ultrabook", "lightweight", "travel"), "Taiwan"),
                createProduct(26L, "CoreBook 14 Everyday Laptop", "699.99", "Laptops", 50,
                        "CRB-BK14-8-512", 4.3, 1420, List.of("budget", "everyday", "laptop"), "China"),
                createProduct(27L, "HomePro 15 Standard Laptop", "649.99", "Laptops", 45,
                        "HMP-PR15-8-512", 4.2, 1280, List.of("budget", "home", "laptop"), "China"),
                createProduct(28L, "ZenCore 14 Slim Laptop", "899.99", "Laptops", 35,
                        "ZEN-CR14-16-512", 4.5, 870, List.of("slim", "midrange", "laptop"), "Taiwan"),
                createProduct(29L, "WorkMate T14 Business Laptop", "1199.99", "Laptops", 28,
                        "WRK-T14-16-512", 4.6, 620, List.of("business", "durable", "laptop"), "Japan"),
                createProduct(30L, "ProEdge 14 High-Performance Laptop", "1999.99", "Laptops", 22,
                        "PRE-DG14-32-1TB", 4.9, 480, List.of("high-performance", "creator", "laptop"), "USA"),

                createProduct(31L, "NovaTab S10 Tablet", "499.99", "Tablets", 60,
                        "NVA-S10-64", 4.4, 950, List.of("tablet", "android", "midrange"), "South Korea"),
                createProduct(32L, "Orion Slate X Tablet", "649.99", "Tablets", 45,
                        "ORN-SLX-128", 4.5, 720, List.of("tablet", "android", "premium"), "China"),
                createProduct(33L, "VeloTab Air 11 Tablet", "399.99", "Tablets", 70,
                        "VLO-AR11-64", 4.2, 1050, List.of("tablet", "android", "budget"), "China"),
                createProduct(34L, "ZenithPad Pro 12 Tablet", "799.99", "Tablets", 35,
                        "ZPD-PR12-256", 4.7, 560, List.of("tablet", "pro", "creator"), "Japan"),
                createProduct(35L, "AstraTab Lite 10 Tablet", "299.99", "Tablets", 80,
                        "ASR-LT10-32", 4.1, 1280, List.of("tablet", "budget", "entry"), "China"),
                createProduct(36L, "CoreSlate Mini 8 Tablet", "249.99", "Tablets", 90,
                        "CRS-MN8-32", 4.0, 1420, List.of("tablet", "mini", "budget"), "China"),

                createProduct(37L, "Orion X12 Smartphone", "999.99", "Smartphones", 50,
                        "ORN-X12-256", 4.6, 2240, List.of("smartphone", "flagship", "5g"), "South Korea"),
                createProduct(38L, "NovaPhone Z Pro Smartphone", "899.99", "Smartphones", 65,
                        "NVA-ZPR-256", 4.5, 1980, List.of("smartphone", "premium", "5g"), "China"),
                createProduct(39L, "VeloCore S9 Smartphone", "749.99", "Smartphones", 70,
                        "VLO-S9-128", 4.4, 2340, List.of("smartphone", "midrange", "5g"), "China"),
                createProduct(40L, "Astra One Max Smartphone", "1099.99", "Smartphones", 40,
                        "ASR-ONX-512", 4.7, 1340, List.of("smartphone", "flagship", "camera"), "USA"),
                createProduct(41L, "Zenith Edge 5G Smartphone", "849.99", "Smartphones", 55,
                        "ZEN-EDG-256", 4.5, 1680, List.of("smartphone", "premium", "5g"), "Japan"),
                createProduct(42L, "CoreLink Lite 5 Smartphone", "599.99", "Smartphones", 85,
                        "CRL-LT5-128", 4.2, 2680, List.of("smartphone", "budget", "5g"), "China"),

                createProduct(43L, "NovaWatch Pro Smartwatch", "299.99", "Smartwatches", 75,
                        "NVA-WPR-BLK", 4.6, 1240, List.of("smartwatch", "fitness", "premium"), "South Korea"),
                createProduct(44L, "Orion Fit X Smartwatch", "249.99", "Smartwatches", 80,
                        "ORN-FTX-BLK", 4.4, 1580, List.of("smartwatch", "fitness", "midrange"), "China"),
                createProduct(45L, "VeloPulse S Smartwatch", "199.99", "Smartwatches", 95,
                        "VLO-PLS-BLK", 4.3, 1840, List.of("smartwatch", "fitness", "budget"), "China"),
                createProduct(46L, "Zenith Time Pro Smartwatch", "349.99", "Smartwatches", 50,
                        "ZEN-TPR-SLV", 4.7, 910, List.of("smartwatch", "premium", "luxury"), "Japan"),
                createProduct(47L, "AstraFit Core Smartwatch", "179.99", "Smartwatches", 110,
                        "ASR-FTC-BLK", 4.2, 2140, List.of("smartwatch", "fitness", "budget"), "China"),
                createProduct(48L, "CoreWear Lite Smartwatch", "149.99", "Smartwatches", 120,
                        "CRW-LTE-BLK", 4.0, 2530, List.of("smartwatch", "entry", "budget"), "China")
        );
    }

    private Product createProduct(Long id, String name, String price, String category, int stock,
                                  String sku, double rating, int popularity, List<String> tags, String warehouseCountry) {
        return new Product(id, name, new BigDecimal(price), category, stock, sku, rating, popularity, tags, warehouseCountry);
    }
}
