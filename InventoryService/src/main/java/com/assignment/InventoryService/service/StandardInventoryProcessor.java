package com.assignment.InventoryService.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.assignment.InventoryService.persistence.entity.InventoryBatch;
import com.assignment.InventoryService.persistence.repository.InventoryRepository;
import com.assignment.InventoryService.rest.model.BatchDetail;
import com.assignment.InventoryService.rest.model.InventoryResponse;

import jakarta.transaction.Transactional;

@Service("StandardInventoryProcessor")
@RequiredArgsConstructor
public class StandardInventoryProcessor implements InventoryProcessor {
    private final InventoryRepository repository;

    @Override
    public List<InventoryBatch> getBatches(Long productId) {
        // Requirement: Sort by expiry date
        return repository.findByProductIdOrderByExpiryDateAsc(productId);
    }

    @Override
    @Transactional
    public void updateStock(Long productId, int quantity) {
        // Logic to deduct quantity from batches in the order of expiry date
        List<InventoryBatch> batches = getBatches(productId);
        int remainingToDeduct = quantity;

        for (InventoryBatch batch : batches) {
            if (remainingToDeduct <= 0)
                break;

            int available = batch.getQuantity();
            if (available >= remainingToDeduct) {
                batch.setQuantity(available - remainingToDeduct);
                remainingToDeduct = 0;
            } else {
                batch.setQuantity(0);
                remainingToDeduct -= available;
            }
            repository.save(batch);
        }

        if (remainingToDeduct > 0) {
            throw new RuntimeException("Insufficient Stock");
        }
    }

    @Override
    public InventoryResponse getProductInventoryResponse(Long productId) {
        List<InventoryBatch> batches = getBatches(productId);

        if (batches.isEmpty()) {
            return null;
        }

        InventoryBatch firstBatch = batches.get(0);
        List<BatchDetail> batchDetails = batches.stream()
                .map(batch -> new BatchDetail(batch.getBatchId(), batch.getQuantity(), batch.getExpiryDate()))
                .collect(Collectors.toList());

        return new InventoryResponse(
                firstBatch.getProductId(),
                firstBatch.getProductName(),
                batchDetails);
    }
}