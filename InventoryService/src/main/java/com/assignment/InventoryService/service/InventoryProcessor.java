package com.assignment.InventoryService.service;

import java.util.List;

import com.assignment.InventoryService.persistence.entity.InventoryBatch;
import com.assignment.InventoryService.rest.model.InventoryResponse;

public interface InventoryProcessor {
    List<InventoryBatch> getBatches(Long productId);

    void updateStock(Long productId, int quantity);

    InventoryResponse getProductInventoryResponse(Long productId);
}