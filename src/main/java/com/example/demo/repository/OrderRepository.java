package com.example.demo.repository;

import com.example.demo.domain.Order;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private final List<Order> orders = new ArrayList<>();

    @Nonnull
    public Order save(@Nonnull Order order) {
        orders.add(order);
        return order;
    }

    @Nonnull
    public List<Order> findAll() {
        return new ArrayList<>(orders);
    }

    @Nonnull
    public Optional<Order> findById(@Nonnull String orderId) {
        return orders.stream().filter(order -> order.id().equals(orderId)).findFirst();
    }

    @Nonnull
    public List<Order> findByProductCode(@Nonnull String productCode) {
        return orders.stream().filter(order -> order.productCode().equals(productCode)).toList();
    }

    public long count() {
        return orders.size();
    }

    public void deleteAll() {
        orders.clear();
    }
}
