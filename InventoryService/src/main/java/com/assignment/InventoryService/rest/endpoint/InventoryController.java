package com.assignment.InventoryService.rest.endpoint;

import com.assignment.InventoryService.rest.model.InventoryResponse;
import com.assignment.InventoryService.rest.model.UpdateRequest;
import com.assignment.InventoryService.service.InventoryProcessor;
import com.assignment.InventoryService.service.InventoryProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryProcessorFactory factory;

    /**
     * Endpoint: GET /inventory/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        // Use factory to get the processor (Defaulting to standard)
        InventoryProcessor processor = factory.getProcessor("StandardInventoryProcessor");

        InventoryResponse response = processor.getProductInventoryResponse(productId);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: POST /inventory/update
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateInventory(@RequestBody UpdateRequest request) {
        InventoryProcessor processor = factory.getProcessor("StandardInventoryProcessor");

        try {
            processor.updateStock(request.getProductId(), request.getQuantity());
            return ResponseEntity.ok("Inventory updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
