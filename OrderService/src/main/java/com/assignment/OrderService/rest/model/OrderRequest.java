package com.assignment.OrderService.rest.model;

// DTO class
@lombok.Data
public class OrderRequest {
    private Long productId;
    private int quantity;
}
