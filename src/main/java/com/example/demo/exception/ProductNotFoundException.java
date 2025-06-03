package com.example.demo.exception;

public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String productCode) {
        super("Product not found: " + productCode);
    }
}
