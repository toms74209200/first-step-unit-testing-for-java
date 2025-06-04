package com.example.demo.domain;

public enum OrderType {
    NORMAL("通常注文"),
    RESERVATION("予約注文");

    private final String displayName;

    OrderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
