package com.example.demo.repository;

import com.example.demo.domain.Product;
import com.example.demo.exception.ProductNotFoundException;
import jakarta.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    private final List<Product> products;

    public ProductRepository() {
        this.products =
                Arrays.asList(
                        new Product("PROD001", "iPhone 15", BigDecimal.valueOf(120000), 50, false),
                        new Product(
                                "PROD002", "MacBook Pro", BigDecimal.valueOf(280000), 25, false),
                        new Product("PROD003", "iPad Air", BigDecimal.valueOf(80000), 30, false),
                        new Product(
                                "PROD004", "AirPods Pro", BigDecimal.valueOf(35000), 100, false),
                        new Product("PROD005", "Apple Watch", BigDecimal.valueOf(50000), 75, false),
                        new Product(
                                "BOOK001", "Spring Boot入門", BigDecimal.valueOf(3200), 200, false),
                        new Product("BOOK002", "Java完全ガイド", BigDecimal.valueOf(4500), 150, false),
                        new Product("ELEC001", "ワイヤレスキーボード", BigDecimal.valueOf(8000), 80, false),
                        new Product("ELEC002", "4Kモニター", BigDecimal.valueOf(45000), 15, false),
                        new Product(
                                "GAME001", "Nintendo Switch", BigDecimal.valueOf(32000), 60, false),
                        new Product(
                                "PRE001", "iPhone 16 Pro（予約）", BigDecimal.valueOf(150000), 0, true),
                        new Product(
                                "PRE002",
                                "MacBook Air M3（予約）",
                                BigDecimal.valueOf(180000),
                                0,
                                true));
    }

    @Nonnull
    public Optional<Product> findByCode(@Nonnull String productCode) {
        return products.stream().filter(product -> product.code().equals(productCode)).findFirst();
    }

    public void updatedProduct(@Nonnull Product product) throws ProductNotFoundException {
        if (products.stream().noneMatch(p -> p.code().equals(product.code()))) {
            throw new ProductNotFoundException(String.format("商品コード %s は存在しません。", product.code()));
        }
        synchronized (this) {
            products.replaceAll(p -> p.code().equals(product.code()) ? product : p);
        }
    }
}
