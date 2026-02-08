package com.assignment.InventoryService.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDetail {
    private Long batchId;
    private Integer quantity;
    private LocalDate expiryDate;
}
