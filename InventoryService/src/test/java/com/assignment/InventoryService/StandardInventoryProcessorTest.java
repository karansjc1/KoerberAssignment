package com.assignment.InventoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.assignment.InventoryService.persistence.entity.InventoryBatch;
import com.assignment.InventoryService.persistence.repository.InventoryRepository;
import com.assignment.InventoryService.service.StandardInventoryProcessor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandardInventoryProcessorTest {

    @Mock
    private InventoryRepository repository;

    @InjectMocks
    private StandardInventoryProcessor processor;

    private List<InventoryBatch> mockBatches;

    @BeforeEach
    void setUp() {
        // Prepare mock data: 2 batches with different expiry dates
        InventoryBatch batch1 = new InventoryBatch(1L, 1001L, "Laptop", 10, LocalDate.of(2025, 1, 1)); // Expiring
        InventoryBatch batch2 = new InventoryBatch(2L, 1001L, "Laptop", 20, LocalDate.of(2025, 12, 31)); // Expiring
        // Use a mutable list so we can modify quantities in tests
        mockBatches = new ArrayList<>(Arrays.asList(batch1, batch2));
    }

    @Test
    void testGetBatches_ShouldReturnSortedByExpiryDate() {
        when(repository.findByProductIdOrderByExpiryDateAsc(1001L)).thenReturn(mockBatches);

        List<InventoryBatch> result = processor.getBatches(1001L);

        // Assert
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2025, 1, 1), result.get(0).getExpiryDate(), "First item should expire sooner");
        verify(repository, times(1)).findByProductIdOrderByExpiryDateAsc(1001L);
    }

    @Test
    void testUpdateStock_ShouldDeductFromFirstBatch_WhenQuantityIsEnough() {
        when(repository.findByProductIdOrderByExpiryDateAsc(1001L)).thenReturn(mockBatches);

        processor.updateStock(1001L, 5);

        // Assert
        assertEquals(5, mockBatches.get(0).getQuantity(), "Batch 1 should be reduced to 5");
        assertEquals(20, mockBatches.get(1).getQuantity(), "Batch 2 should remain untouched");
        verify(repository, times(1)).save(mockBatches.get(0)); // Only batch 1 needed saving
    }

    @Test
    void testUpdateStock_ShouldDeductFromMultipleBatches_WhenFirstBatchIsNotEnough() {
        when(repository.findByProductIdOrderByExpiryDateAsc(1001L)).thenReturn(mockBatches);

        processor.updateStock(1001L, 15);

        // Assert
        assertEquals(0, mockBatches.get(0).getQuantity(), "Batch 1 should be empty");
        assertEquals(15, mockBatches.get(1).getQuantity(), "Batch 2 should be reduced by remaining 5");

        verify(repository, times(1)).save(mockBatches.get(0));
        verify(repository, times(1)).save(mockBatches.get(1));
    }

    @Test
    void testUpdateStock_ShouldThrowException_WhenInsufficientStock() {
        when(repository.findByProductIdOrderByExpiryDateAsc(1001L)).thenReturn(mockBatches);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            processor.updateStock(1001L, 50); // Requesting more than available
        });

        assertTrue(exception.getMessage().contains("Insufficient Stock"));
    }
}
