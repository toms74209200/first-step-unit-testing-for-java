package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderType;
import com.example.demo.domain.Product;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.InvalidOrderException;
import com.example.demo.exception.OrderFailedException;
import com.example.demo.model.OrderRequest;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class OrderServiceTest {

    // 存在する商品コードと数量で注文したとき、成功する
    @Test
    public void testprocessOrderSucceeded() throws Exception {
        String expectedProductCode = "test";
        int expectedQuantity = new Random().nextInt(1, 10);
        Instant before = Instant.now();

        // Arrange: 注文の数量が在庫数以下のとき
        int stock = expectedQuantity + 1;

        ProductRepository productRepositoryMock = mock(ProductRepository.class);
        // Arrange: 注文の商品コードが存在するとき
        when(productRepositoryMock.findByCode(anyString()))
                .thenReturn(
                        Optional.of(
                                new Product(
                                        expectedProductCode,
                                        "Test Product",
                                        BigDecimal.valueOf(100),
                                        stock,
                                        false)));
        OrderRepository orderRepositoryMock = mock(OrderRepository.class);
        when(orderRepositoryMock.save(any(Order.class)))
                .thenReturn(
                        new Order(
                                "test-id",
                                expectedProductCode,
                                expectedQuantity,
                                BigDecimal.valueOf(100),
                                Instant.now(),
                                OrderType.NORMAL));

        OrderService orderService = new OrderService(productRepositoryMock, orderRepositoryMock);

        // Act: 注文する
        Order order;
        try {
            order =
                    orderService.processOrder(
                            new OrderRequest(expectedProductCode, expectedQuantity));
        } catch (InvalidOrderException | InsufficientStockException | OrderFailedException e) {
            e.printStackTrace();
            fail();
            return;
        }

        assertThat(order.id()).isNotBlank();
        // Assert: 注文の商品コードが一致するか
        assertThat(order.productCode()).isEqualTo(expectedProductCode);
        // Assert: 注文の数量が一致するか
        assertThat(order.quantity()).isEqualTo(expectedQuantity);
        // Assert: 注文の金額が正しいか
        assertThat(order.amount().longValue()).isGreaterThan(0);
        assertThat(order.timestamp().toEpochMilli()).isGreaterThanOrEqualTo(before.toEpochMilli());

        // Assert: DBに保存されているか
        verify(orderRepositoryMock).save(any(Order.class));
        verify(productRepositoryMock).updatedProduct(any(Product.class));
    }

    // 存在する予約商品の商品コードで注文したとき、成功する
    @Test
    public void testProcessOrderWhenReservationOrderSucceeded() throws Exception {
        String expectedProductCode = "reservation-test";
        int expectedQuantity = new Random().nextInt(1, 10);
        Instant before = Instant.now();
        int stock = 10;

        ProductRepository productRepositoryMock = mock(ProductRepository.class);
        // Arrange: 商品が予約商品のとき
        // Arrange: 注文の商品コードが存在するとき
        when(productRepositoryMock.findByCode(anyString()))
                .thenReturn(
                        Optional.of(
                                new Product(
                                        expectedProductCode,
                                        "Reservation Product",
                                        BigDecimal.valueOf(100),
                                        stock,
                                        true)));
        OrderRepository orderRepositoryMock = mock(OrderRepository.class);
        when(orderRepositoryMock.save(any(Order.class)))
                .thenReturn(
                        new Order(
                                "reservation-test-id",
                                expectedProductCode,
                                expectedQuantity,
                                BigDecimal.valueOf(100),
                                Instant.now(),
                                OrderType.RESERVATION));

        OrderService orderService = new OrderService(productRepositoryMock, orderRepositoryMock);

        // Act: 注文する
        Order order;
        try {
            order =
                    orderService.processOrder(
                            new OrderRequest(expectedProductCode, expectedQuantity));
        } catch (InvalidOrderException | InsufficientStockException | OrderFailedException e) {
            e.printStackTrace();
            fail();
            return;
        }

        assertThat(order.id()).isNotBlank();
        // Assert: 注文の商品コードが一致するか
        assertThat(order.productCode()).isEqualTo(expectedProductCode);
        // Assert: 注文の数量が一致するか
        assertThat(order.quantity()).isEqualTo(expectedQuantity);
        // Assert: 注文の金額が正しいか
        assertThat(order.amount().longValue()).isGreaterThan(0);
        assertThat(order.timestamp().toEpochMilli()).isGreaterThanOrEqualTo(before.toEpochMilli());

        // Assert: DBに保存されているか
        verify(orderRepositoryMock).save(any(Order.class));
        // Assert: 予約商品の場合、商品情報の更新は行わないか
        verify(productRepositoryMock, never()).updatedProduct(any(Product.class));
    }
}
