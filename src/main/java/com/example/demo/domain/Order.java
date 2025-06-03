package com.example.demo.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Order(
        String id, String productCode, int quantity, BigDecimal amount, Instant timestamp) {}
