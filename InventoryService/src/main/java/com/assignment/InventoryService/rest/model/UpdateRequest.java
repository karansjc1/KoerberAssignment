package com.assignment.InventoryService.rest.model;

import lombok.Data;

@Data
public class UpdateRequest {
    private Long productId;
    private int quantity;
}