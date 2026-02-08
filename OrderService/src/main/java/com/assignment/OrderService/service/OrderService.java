package com.assignment.OrderService.service;

import com.assignment.OrderService.persistence.entity.Order;
import com.assignment.OrderService.persistence.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.assignment.OrderService.rest.model.OrderRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final RestTemplate restTemplate;

    // In a real app, this would be in application.yaml
    private final String INVENTORY_SERVICE_URL = "http://localhost:8081/inventory/update";

    public Order placeOrder(OrderRequest request) {
        try {
            // Prepare the payload for Inventory Service
            Map<String, Object> inventoryRequest = new HashMap<>();
            inventoryRequest.put("productId", request.getProductId());
            inventoryRequest.put("quantity", request.getQuantity());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(inventoryRequest, headers);

            // POST call to Inventory Service
            restTemplate.postForObject(INVENTORY_SERVICE_URL, entity, String.class);

        } catch (Exception e) {
            // If inventory update fails (e.g. 400 Bad Request due to insufficient stock),
            // we re-throw to prevent order placement.
            throw new RuntimeException("Failed to place order: " + e.getMessage());
        }

        // 2. If successful, save the order locally
        Order order = new Order();
        order.setProductId(request.getProductId());
        order.setProductName("Product " + request.getProductId()); // Simplified as we don't fetch name
        order.setQuantity(request.getQuantity());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());

        return repository.save(order);
    }
}