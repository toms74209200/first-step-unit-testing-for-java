package com.example.demo.domain;

import java.math.BigDecimal;

public record Product(String code, String name, BigDecimal price, int stock) {}
