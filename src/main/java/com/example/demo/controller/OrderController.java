package com.example.demo.controller;

import com.example.demo.domain.Order;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.InvalidOrderException;
import com.example.demo.model.OrderRequest;
import com.example.demo.model.OrderResponse;
import com.example.demo.service.OrderService;
import jakarta.annotation.Nonnull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    public final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Nonnull
    public ResponseEntity<?> postOrder(@RequestBody @Validated OrderRequest request)
            throws Exception {
        Order order;
        try {
            order = orderService.processOrder(request);
        } catch (InsufficientStockException | InvalidOrderException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        OrderResponse response =
                new OrderResponse(
                        order.id(), order.amount().longValue(), order.timestamp().toString());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInsufficientStockException(Exception ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }
}
