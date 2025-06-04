package com.example.demo.service;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderType;
import com.example.demo.domain.Product;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.InvalidOrderException;
import com.example.demo.exception.OrderFailedException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.OrderRequest;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Nonnull
    public Order processOrder(@Nonnull OrderRequest request)
            throws InvalidOrderException, InsufficientStockException, OrderFailedException {
        Product product =
                productRepository
                        .findByCode(request.productCode())
                        .orElseThrow(
                                () ->
                                        new InvalidOrderException(
                                                String.format(
                                                        "商品コード %s は存在しません。",
                                                        request.productCode())));

        if (product.isReservation()) {
            return processReservationOrder(product, request);
        } else {
            return processNormalOrder(product, request);
        }
    }

    private Order processNormalOrder(Product product, OrderRequest request)
            throws InsufficientStockException, OrderFailedException {

        if (request.quantity() > product.stock()) {
            throw new InsufficientStockException(
                    String.format(
                            "商品コード %s の在庫が不足しています。要求数量: %d, 在庫数: %d",
                            request.productCode(), request.quantity(), product.stock()));
        }
        Order order = createOrder(product, request, OrderType.NORMAL);

        try {
            productRepository.updatedProduct(
                    new Product(
                            product.code(),
                            product.name(),
                            product.price(),
                            product.stock() - request.quantity(),
                            product.isReservation()));
        } catch (ProductNotFoundException e) {
            throw new OrderFailedException(String.format("商品コード %s の更新に失敗しました。", product.code()));
        }

        return orderRepository.save(order);
    }

    private Order processReservationOrder(Product product, OrderRequest request) {

        Order order = createOrder(product, request, OrderType.RESERVATION);
        return orderRepository.save(order);
    }

    private Order createOrder(Product product, OrderRequest request, OrderType orderType) {
        String orderId = UUID.randomUUID().toString();
        BigDecimal amount = product.price().multiply(BigDecimal.valueOf(request.quantity()));
        Instant timestamp = Instant.now();

        return new Order(
                orderId, request.productCode(), request.quantity(), amount, timestamp, orderType);
    }
}
