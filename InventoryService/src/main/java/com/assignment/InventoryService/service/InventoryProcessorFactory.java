package com.assignment.InventoryService.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryProcessorFactory {
    private final Map<String, InventoryProcessor> processors;

    public InventoryProcessor getProcessor(String tenantOrType) {
        // For now, we return the standard one, but this allows extension
        return processors.getOrDefault(tenantOrType, processors.get("StandardInventoryProcessor"));
    }
}