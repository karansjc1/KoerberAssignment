package com.assignment.OrderService.service;

import com.assignment.OrderService.persistence.entity.Order;
import com.assignment.OrderService.persistence.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.assignment.OrderService.rest.model.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final RestTemplate restTemplate;

    @Value("${inventory-service.url}")
    private String inventoryServiceBaseUrl;

    @Value("${inventory-service.update-url}")
    private String inventoryServiceUpdateUrl;

    public Order placeOrder(OrderRequest request) {
        try {
            String productName = null;

            String productUrl = inventoryServiceBaseUrl + "/" + request.getProductId();
            String inventoryResponse = restTemplate.getForObject(productUrl, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(inventoryResponse, Map.class);
            productName = (String) responseMap.get("productName");

            Map<String, Object> inventoryRequest = new HashMap<>();
            inventoryRequest.put("productId", request.getProductId());
            inventoryRequest.put("quantity", request.getQuantity());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(inventoryRequest, headers);

            restTemplate.postForObject(inventoryServiceUpdateUrl, entity, String.class);

            Order order = new Order();
            order.setProductId(request.getProductId());
            order.setProductName(productName);
            order.setQuantity(request.getQuantity());
            order.setStatus("PLACED");
            order.setOrderDate(LocalDate.now());

            return repository.save(order);

        } catch (Exception e) {
            throw new RuntimeException("Failed to place order: " + e.getMessage());
        }
    }
}